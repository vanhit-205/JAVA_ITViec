#!/bin/bash
# Wait for MySQL to be ready

MAX_RETRIES=30
RETRY_INTERVAL=2

echo "Waiting for MySQL to be ready..."

for i in $(seq 1 $MAX_RETRIES); do
    if mysqladmin ping -h mysql -u root -p"${MYSQL_ROOT_PASSWORD:-123456789}" --silent 2>/dev/null; then
        echo "MySQL is ready!"
        exit 0
    fi
    echo "Attempt $i/$MAX_RETRIES: MySQL not ready yet..."
    sleep $RETRY_INTERVAL
done

echo "ERROR: MySQL did not become ready in time"
exit 1
