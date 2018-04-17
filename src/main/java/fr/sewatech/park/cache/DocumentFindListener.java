package fr.sewatech.park.cache;

import com.mongodb.DBRef;
import fr.sewatech.park.data.AbstractDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentFindListener extends AbstractMongoEventListener<AbstractDocument> {

    private final CacheManager cacheManager;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // Avoid wrong warning for cacheManager
    public DocumentFindListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Intercept a _find_ call, before the mapping occurs,
     * it replaces DBRef objects with entities,
     * only if the relation is annotated with @{@link CacheableRef}
     */
    @Override
    public void onAfterLoad(AfterLoadEvent<AbstractDocument> event) {
        Document document = event.getDocument();
        CacheableField.fromClass(event.getType())
                .stream()
                .map(CachedField::new)
                .map(cachedField -> cachedField.fromCache(document))
                .filter(cachedField -> cachedField.entity != null)
                .forEach(cachedField -> cachedField.toDocument(document));
    }

    /**
     * Intercept a _find_ call, after the mapping occurs,
     * it put the related entity in the cache
     * only if the relation is annotated with @{@link CacheableRef}
     */
    @Override
    public void onAfterConvert(AfterConvertEvent<AbstractDocument> event) {
        AbstractDocument rootEntity = event.getSource();
        CacheableField.fromClass(rootEntity.getClass())
                .stream()
                .map(CachedField::new)
                .map(cachedField -> cachedField.fromEntity(rootEntity))
                .forEach(CachedField::toCache);
    }

    private static class CacheableField {
        final String fieldName;
        final String cacheName;

        CacheableField(String fieldName, String cacheName) {
            this.fieldName = fieldName;
            this.cacheName = cacheName;
        }

        static List<CacheableField> fromClass(Class<?> type) {
            Field[] fields = type.getDeclaredFields();
            List<CacheableField> cacheableFields = new ArrayList<>();
            for (Field field : fields) {
                CacheableRef cacheableRef = field.getDeclaredAnnotation(CacheableRef.class);
                if (cacheableRef != null) {
                    cacheableFields.add(new CacheableField(field.getName(), cacheableRef.cacheName()));
                }
            }

            return cacheableFields;
        }
    }

    private class CachedField {
        final CacheableField field;
        final String fieldName;
        AbstractDocument entity;

        CachedField(CacheableField field) {
            this.field = field;
            this.fieldName = field.fieldName;
        }

        @SuppressWarnings("ConstantConditions") // Avoid wrong warning for (wrapper != null)
        private CachedField fromCache(Document rootDocument) {
            DBRef dbRef = (DBRef) rootDocument.get(field.fieldName);
            if (dbRef == null) {
                return this;
            }

            Cache cache = cacheManager.getCache(field.cacheName);
            Cache.ValueWrapper wrapper = cache.get(dbRef.getId());
            if (wrapper != null) {
                entity = (AbstractDocument) wrapper.get();
            }
            return this;
        }

        private CachedField fromEntity(AbstractDocument rootDocument) {
            try {
                Field declaredField = rootDocument.getClass().getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                Object fieldValue = declaredField.get(rootDocument);

                if (!(fieldValue instanceof LazyLoadingProxy)) {
                    if (fieldValue instanceof AbstractDocument) {
                        entity = (AbstractDocument) fieldValue;
                    } else if (fieldValue instanceof Iterable) {
                        // TODO
                    }
                }
            } catch (Exception e) {
                // Skip the problem
            }
            return this;
        }

        private CachedField toCache() {
            // No entity if field is empty or lazy
            if (entity != null) {
                Cache cache = cacheManager.getCache(this.field.cacheName);
                cache.putIfAbsent(this.entity.getId(), this.entity);
            }
            return this;
        }

        private CachedField toDocument(Document rootDocument) {
            rootDocument.put(this.fieldName, this.entity);
            return this;
        }
    }

}
