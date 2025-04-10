#!/bin/sh

echo "Generating .env file..."
cat <<EOF > .env
DATABASE_URL=${DATABASE_URL}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${DATABASE_PASSWORD}
EOF

echo ".env generated ✅"
exec java -jar app.jar

