
echo '>>>>>>>> start PG'
docker-entrypoint.sh postgres > /var/log/postgres.log &

## Waiting for the log to say ready for start up
echo '>>>>>>>> waiting for db to start'
#sleep 10
( tail -f -n0 /var/log/postgres.log & ) | grep -q "ready to accept connections"
echo '>>>>>>>> DONE waiting for db to start'


psql -U postgres -f 00_create-db-and-user.sql 
psql -U j8ql_user -d j8ql_db -f 01_create-tables.sql
echo '>>>>>>>> READY'

## Note: Somehow the 'docker-entrypoint.sh' I think will trigger a quick shutdown and restart of the DB at this point. 
##       But does not seem to impact much.

# To keep the process open
# Note: there should be a better way 
tail -f /var/log/postgres.log

