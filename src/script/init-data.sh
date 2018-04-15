base_url=http://localhost:8080

curl -X POST \
  $base_url/brand \
  -H 'content-type: application/json' \
  -d '{"id": "b1", "name":"BrandX"}'
echo

curl -X POST \
  $base_url/brand \
  -H 'content-type: application/json' \
  -d '{"id": "b2", "name":"BrandY"}'
echo

curl -X POST \
  $base_url/brand \
  -H 'content-type: application/json' \
  -d '{"id": "b3", "name":"BrandZ"}'
echo

curl -X POST \
  $base_url/city \
  -H 'content-type: application/json' \
  -d '{"id": "c1", "name":"CityA"}'
echo
curl -X POST \
  $base_url/city \
  -H 'content-type: application/json' \
  -d '{"id": "c2", "name":"CityB"}'
echo

curl -X POST \
  $base_url/concession \
  -H 'content-type: application/json' \
  -d '{"id": "z1", "name":"Concession1", "city": {"id": "c1"},"brands":[{"id":"b1"}]}'
echo
curl -X POST \
  $base_url/concession \
  -H 'content-type: application/json' \
  -d '{"id": "z2", "name":"Concession2", "city": {"id": "c2"},"brands":[{"id":"b1"}, {"id":"b3"}]}'
echo
curl -X POST \
  $base_url/concession \
  -H 'content-type: application/json' \
  -d '{"id": "z3", "name":"Concession2", "city": {"id": "c2"},"brands":[{"id":"b2"}]}'
echo
