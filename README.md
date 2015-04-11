J8QL
=====
#### J8QL is a lightweight SQL centric ORM  for Java 8

- **SQL Centric:** Fluid, flexible, progressive, thread safe, and OO friendly scheme to build and execute SQL queries. 
- **Schema Aware:** Scan (and cache) schema metadata to minimize redundancy between database schema and Java Class definitions.
- **Java 8 Centric:** Written in Java 8 for Java 8.
- **Postgresql First:**  Depth before breath. Going deep on PostgreSQL first.

**Current version:** 0.5-SNAPSHOT *(Under development, relatively robust, for postgres 9.3 only, API might change)* 

**License:** Apache v2

[J8QL by example](#j8ql-by-example) | [Maven Info](#maven-info) | [Key Concepts](#key-concepts) | [Why not Hibernate?](#why-not-hibernate) | [Why J8QL?](#why-j8ql)

### J8QL by example

First, J8QL provide a simple and java friendly way to run raw sql statements with Java 8. You can list or stream the result, and get it as a generic set of Map (i.e. Record) or typed object. 

```java
// ------ raw SQLs execute ------ //
// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
DB db = new DBBuilder().build(dataSource);

// 1 runner == 1 database connection
try (Runner runner = db.openRunner()) {

  // Execute raw SQL insert with parameters
  runner.execute("insert into \"user\" (id,username,since) values (?,?,?)",
                 12L,"john",1997);

  // Execute a SQL select and return a list of Record (Record implements Map)
  List<Record> records = runner.list("select * from \"user\" where id = ?",12L);
  // users.size() == 1
  // users.get(0).get("username") == "john"

  // Execute same select but return as User.class
  List<User> users = runner.list(User.class, "select * from \"user\" where id = ?", 12L);
  // users.get(0).getUsername() == "john"

  // Execute a sql select as stream
  try (Stream<User> stream = runner.stream(User.class,"select * from \"user\" where id = ?",12L)){
    User johnUser = stream.findFirst().get();
    assertEquals(Long.valueOf(12L), johnUser.getId());
    // johnUser.getUsername() == "john"
  } // stream will be closed, which will close the enclosing PreparedStatement

} // J8QL runner will be closed as well as the enclosing DB connection

// ------ /raw SQLs execute ------ //
```

Second, J8QL provide SQL Centric query building facilities that allow to build simple to complex SQL queries in a completely threadsafe and Java friendly pattern.

```java
// ------ Query Builder Examples ------ //
DB db = new DBBuilder().build(dataSource);

try (Runner runner = db.openRunner()){

  // Simple Insert Query via columns and values
  InsertQuery<Integer> insertJohn = Query.insert("user").columns("id", "username", "since")
                                  .values(12L, "john", 1997);
  // execute the insert (by default, return the numOfRowChanged -same as PreprateStatement.executeUpdate)
  int numOfRowChanged = runner.exec(insertJohn);
  assertEquals(numOfRowChanged, 1);

  // mapOf is an static utility to create name/value hashmap
  Map jenMap = mapOf("id",13L,"username","jen","since",2004);

  // Insert a Map or Pojo object, and returning the table PK as type Long
  InsertQuery<Long> insertJen = Query.insert("user").value(jenMap).returningIdAs(Long.class);
  // execute the insert
  Long jenId = runner.exec(insertJen);
  assertEquals(13L, jenId.longValue());

  // Using Select Query to list all User.class
  SelectQuery<User> selectUsers = Query.select(User.class).orderBy("since");

  // List all users
  List<User> users = runner.list(selectUsers);
  assertEquals(12L, users.get(0).getId().longValue());
  assertEquals(13L, users.get(1).getId().longValue());

  // Select the first and add a condition to an existing Query
  // Note 1: Query objects immutable and any call return new ones (so, completely thread safe)
  // Note 2: Runner::first will actually set if needed offset:0 and limit:1 to request only one.
  User jenUser = runner.first(selectUsers.where("username","jen")).get(); // .first return Optional
  assertEquals(13L, jenUser.getId().longValue());

  // Can use stream (make sure to close them)
  try(Stream<User> userStream = runner.stream(selectUsers)){
    List<User> tweentyFirstCenturyUsers = userStream.filter(u -> u.getSince() >= 2000).collect(toList());
    assertEquals(1,tweentyFirstCenturyUsers.size());
    assertEquals("jen", tweentyFirstCenturyUsers.get(0).getUsername());
  }

  // Updating is as trivial, and can even return the whole Object
  // Note: where clause name can have a convenient ";_OPERATOR_"
  UpdateQuery<Integer> updateTo21stCentury = Query.update(User.class).columns("since").values(2000);
  int numOfUpdatedUsers = runner.exec(updateTo21stCentury.where("since;<",2000));
  assertEquals(1,numOfUpdatedUsers);

  // Using a SelectQuery to count
  // Note: t\This will do the appropriate select count... with the query info
  long numOf21stCenturyUsers = runner.count(Query.select("user").where("since;>=",2000));
  assertEquals(2,numOf21stCenturyUsers);

}
// ------ /Query Builder Examples------ //
```

See full readme code at: [org.j8ql.test.ReadmeTest.java](https://github.com/BriteSnow/j8ql/blob/master/src/test/java/org/j8ql/test/ReadmeTest.java)

### Key Concepts

- **SQL Centric:** Rather than trying to fully abstract (and obscure) relational model and SQL from Java, J8QL provides a modern, fluid, and schema aware Java 8 API for SQL. J8QL offers raw sql APIs that cut lot of boilerplate code from JDBC, as well as, a set of Query builder APIs that provide a fluid, thread safe, and Java friendly scheme to build and execute SQL queries. This allows developers to take full advantage of their database SQL in a very Java and OO optimized way.
- **Schema Aware:** J8QL takes the angle to minimize redundancy between Database Schema and Java Class definition by scanning (and caching) the database schema. Why redefine the primary key as Java annotation when we have already have it in the database schema? This model allows to do run something like  ```Optional<User> = runner.first(Query.select(User.class).whereId(12L))``` with zero metadata on the Java side (composite keys also supported by just passing a Map or Pojo object with the appriate name/value properties)
- **Java 8 Centric:** Java 8 offers a great opportunity to re-think Java libraries and J8QL is part of this re-thinking for ORM. J8QL is written in Java 8 for Java 8.
- **Postgresql First:**  Depth before breath. Going deep in one database first allows J8QL to avoid lowest common denominator syndrome and expose the best functionality of one database before adding other ones. For example, J8QL offers seamless support for Postgresql awesome "no-sql/schema-less" capability (i.e. HSTORE datatype, see [postgesql as nosql](http://thebuild.com/presentations/pg-as-nosql-pgday-fosdem-2013.pdf)). 

### Roadmap

1. subselect support.
1. @Table(tableName) and @Column(columnName) is in the plan.
1. Fully nested object support is in the plan. (Currently support one level java object de/serialization to hstore)
1. Seamless support for JSONB.


### Other Examples


#### Join

```java

// Join (select a ticket with its project as projectName)
SelectQuery<Record> selectBuilder = select().from("ticket").leftJoin("project", "id", "ticket", "projectId");
selectBuilder = selectBuilder.columns("ticket.*","project.name projectName");
selectBuilder = selectBuilder.whereId(0);
Optional<Record> rec = runner.first(selectBuilder);
Record record = rec.get();
// project name for ticket 0 is project 0.
assertEquals(projects[0][1],rec.get().get("projectName"));
```

#### Column and Value Expression (since 0.5.4)

As well as being to specify custom operators in Query builders, you can also specify express in column and value
```java
Query.select(Ticket.class).where("lower(subject);=;lower(?)", "UPPER TICKET");
```

Will generate
```sql
select "ticket".* from "ticket" where lower(subject) = lower(?)
```

Full example:

```java
// dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
DB db = new DBBuilder().build(dataSource);

try (Runner runner = db.openRunner()) {
  // insert a tickets (just using raw SQL)
  runner.execute("insert into ticket (id,subject) values (?,?)", 1L, "UPPER ticket");
  runner.execute("insert into ticket (id,subject) values (?,?)", 2L, "lower ticket");

  SelectQuery<Ticket> selectQuery;

  // without the column expression (count of this select should be 0)
  selectQuery = Query.select(Ticket.class).where("subject", "upper ticket");
  System.out.println(db.sql(selectQuery)); // select "ticket".* from "ticket" where "subject" = ?
  assertEquals(0, runner.count(selectQuery));


  // With a custom operator (here ilike for case insensitive)
  selectQuery = Query.select(Ticket.class).where("subject;ilike", "upper ticket");
  // select "ticket".* from "ticket" where "subject" ilike ?
  assertEquals(1, runner.count(selectQuery));

  // With a column expression
  selectQuery = Query.select(Ticket.class).where("lower(subject)", "upper ticket");
  // select "ticket".* from "ticket" where lower(subject) = ?
  assertEquals(1, runner.count(selectQuery));


  // Not that when no operator "=" is used, so the above Query is similar than:
  selectQuery = Query.select(Ticket.class).where("lower(subject);=", "upper ticket");
  // select "ticket".* from "ticket" where lower(subject) = ?
  assertEquals(1, runner.count(selectQuery));

  // With a column expression and value expression
  selectQuery = Query.select(Ticket.class).where("lower(subject);=;lower(?)", "UPPER TICKET");
  // select "ticket".* from "ticket" where lower(subject) = lower(?)
  assertEquals(1, runner.count(selectQuery));
}
```

#### TSV example with Column and Value Expression (since 0.5.4)

The following Java Query building:
```java
Query.select(Ticket.class).where("to_tsvector(ticket.subject);@@;to_tsquery(?)", "management");
```

Will generate the following sql statement:
```sql
select "ticket".* from "ticket" where to_tsvector(ticket.subject) @@ to_tsquery(?)
```

Full example: 

```java
    // dataSource can be built via standard JDBC, or Pool like C3P0 or HikariCP for example
    DB db = new DBBuilder().build(dataSource);

    try (Runner runner = db.openRunner()) {
      // insert a ticket
      runner.execute("insert into ticket (id,subject) values (?,?)", 1L, "test_ticket first ticket for the manager");
      runner.execute("insert into ticket (id,subject) values (?,?)", 2L, "test_ticket second ticket for a manager");
      runner.execute("insert into ticket (id,subject) values (?,?)", 3L, "test_ticket third ticket for a staff");

      // Build the tsv query
      // NOTE 1: You can add third part (with the second ";") and in this case, it will be columnNameOrFunction;operator;valueFunction
      // NOTE 2: The first "columnName" can be a function, and in this case, it won't be escaped;
      // NOTE 3: the last ";to_tsquery(?)" allow to optionally add a function value to the query (which is what we need for tsv search).
      SelectQuery<Ticket> tsvSelect = Query.select(Ticket.class).where("to_tsvector(ticket.subject);@@;to_tsquery(?)", "management");
      System.out.println("sql: " + db.sql(tsvSelect));
      // sql: select "ticket".* from "ticket" where to_tsvector(ticket.subject) @@ to_tsquery(?)

      List<Ticket> tickets = runner.list(tsvSelect);
      assertEquals(2, tickets.size()); // 2 because management and manager have the same lexeme "manag"
    }
```

#### Misc

```java
// execute on a String SQL return the boolean returned by the preparedStatement call
// NOTE: we might change that to match the .execute of a Query. P
boolean b = runner.execute("insert into contact (id,name) values (?,?)", 1,"Mike");

// executeUpdate returns the number of row changed
int r = runner.executeUpdate("insert into contact (id,name) values (?,?)", 2,"Angie");

//  can use the sql returning in pure SQL too
Long jenId = (Long) runner.executeWithReturn("insert into contact (id,name) values (?,?) returning id", 3,"Jen").
```


## Maven Info 
[j8ql Maven version](http://mvnrepository.com/artifact/org.j8ql/j8ql)

Just add the following dependency in your pom.xml (J8QL is on maven central)
```xml
    <dependency>
      <groupId>org.j8ql</groupId>
      <artifactId>j8ql</artifactId>
      <version>0.5.3</version>
    </dependency>   
```


## Why Not Hibernate?

Hibernate is a great full featured ORM library that provides all kind of features, from cross database support, full OO semantic (i.e. collections and relationship), multi level caching, to an high level Object oriented query Language (HQL). We still use it a lot for some of our clients, especially the ones in MySQL and Oracle. 

However, the catch, and there are always a catch in any technology, is that Hibernate heavy SQL abstraction come with the following costs:

1. First, Hibernate heavy SQL abstraction can make simple SQL things much harder to do and developers can endup spending too much time learning how the higher level abstraction translate to lower SQL calls, rather than just learning the language of their database, SQL. **Data is gold** and your **Schema is data's insurance**, consequently, developers should not be afraid of learning the first language of their database. Even the Non-SQL database are now adding SQL!
2. Second, Hibernate static model can make dynamic data representation, which is widely useful in REST/JSON based applications, virtually impossible or highly unoptimized. 
3. And last but not least, I think Hibernate tries solve too many problems, and while it might looks attractive at first, developers that use 100% of hibernate often learn the hard way that some function would have been much better out of their ORM layer (e.g., level 2 caching is a good example). 

So, this is where J8QL comes in.

## Why J8QL?

**J8QL does not attempt to be a framework, but more a lightweight library**

J8QL does not intend to fully abstract the SQL in a pure object oriented semantic, but rather focuses on making SQL as Java 8 and object friendly as possible. This allows developers to take full advantage of their database SQL in a very Java and OO optimized way. J8QL concept is to offer a progressive API from a thing Java friendly JDBC wrapper to entity CRUD based APIs. J8QL supports strongly typed as well a dynamic typing (List and Map), which makes dynamic data representation highly effective and simple. 

J8QL catches, and there are always catches, is that developers needs to be ok with the following: 

- 1) Understand SQL query constructs (e.g joins for joining)

- 2) Manage entity collections and relationships (J8QL does not offer join black magic)

```java
Group group = new Group("NewGroup");

// Hibernate's way (easy but black magic)
contact.getGroups().addGroup(new Group("NewGroup"));
hibernate.save(contact);

// J8QL's way (sql but simple)
Long groupId = (Long) runner.execute(Query.insert("group").value(group).returningIdAs(Long.class));
Map groupContact = mapOf("groupId",groupId,"contactId",contactId);
runner.execute(Query.insert("GroupContact").value(groupContact)); 
```

- 3) To not expect the ORM to provide seamless caching mechanism (use other libraries/service such as Java 8, Guava, ehCache, or memcached), and use data access patterns like DAOs.

If you think that those catches are actually benefits, then, J8QL might be exactly what you are looking for. If not, perhaps sticking with Hibernate might be a better option for you.


