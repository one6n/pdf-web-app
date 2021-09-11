docker run -it --rm --name mongo-pdf-web-app^
 -p 27017:27017 -v C:/fileserver/data/pdf-web-app/db:/data/db^
 -e MONGO_INITDB_ROOT_USERNAME=mongoadmin^
 -e MONGO_INITDB_ROOT_PASSWORD=2v4NdRGvGDZbfrkE^
 mongo