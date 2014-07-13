PSQL = psql
DATABASE = onan
DB_USER = postgres
ifeq ($(shell uname -s),Darwin)
	DB_USER = $(USER)
endif


db-init:
	createdb -U $(DB_USER) $(DATABASE) && \
	cat resources/db.sql | $(PSQL) -U $(DB_USER) -d $(DATABASE)

db-drop:
	cat resources/drop.sql | $(PSQL) -U $(DB_USER)
	dropdb -U $(DB_USER) $(DATABASE)

.PHONY: db-init
