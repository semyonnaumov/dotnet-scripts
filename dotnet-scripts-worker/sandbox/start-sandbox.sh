# $1 - sandbox image
# $2 - sandbox id
# $3 - script from ./test-scripts name
docker rm -f sandbox-"$2" && \
  docker run --name sandbox-"$2" -v "$(pwd)/test-scripts:/tmp/script:ro" \
    --entrypoint "dotnet-script /tmp/script/$3 -c release" \
    "$1"
