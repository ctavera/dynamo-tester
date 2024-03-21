# dynamo-tester
Proyecto para testear un CRUD de DynamoDB localmente.

Instalando a partir de https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html

El proyecto funciona creando la tabla Music manualmente, y despues hace una prueba CRUD con el test.

### Commandos
Ejecutar DynamoDB local, en la carpeta de instalación:
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```
Crear tabla Music con un solo key:
```
 aws dynamodb create-table \
    --table-name Music \
    --attribute-definitions \
        AttributeName=Artist,AttributeType=S \
    --key-schema AttributeName=Artist,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD --endpoint-url http://localhost:8000
```

Agregar elementos a la tabla:
```
aws dynamodb put-item \
--table-name Music --endpoint-url http://localhost:8000 \
--item \
'{"Artist": {"S": "No One You Know"}, "SongTitle": {"S": "Call Me Today"}, "AlbumTitle": {"S": "Somewhat Famous"}, "Awards": {"N": "1"}}'

aws dynamodb put-item \
--table-name Music --endpoint-url http://localhost:8000 \
--item \
'{"Artist": {"S": "No One You Know"}, "SongTitle": {"S": "Howdy"}, "AlbumTitle": {"S": "Somewhat Famous"}, "Awards": {"N": "2"}}'

aws dynamodb put-item \
--table-name Music --endpoint-url http://localhost:8000 \
--item \
'{"Artist": {"S": "Acme Band"}, "SongTitle": {"S": "Happy Day"}, "AlbumTitle": {"S": "Songs About Life"}, "Awards": {"N": "10"}}'

aws dynamodb put-item \
--table-name Music --endpoint-url http://localhost:8000 \
--item \
'{"Artist": {"S": "Acme Band"}, "SongTitle": {"S": "PartiQL Rocks"}, "AlbumTitle": {"S": "Another Album Title"}, "Awards": {"N": "8"}}'
```

Obtener elemento de la tabla:
```
aws dynamodb get-item --consistent-read \
--table-name Music --endpoint-url http://localhost:8000  \
--key '{ "Artist": {"S": "Acme Band"}, "SongTitle": {"S": "Happy Day"}}'
```

Eliminar table:
```
aws dynamodb delete-table --table-name Music --endpoint-url http://localhost:8000
```

Administrador de dynamodb local

https://www.npmjs.com/package/dynamodb-admin
