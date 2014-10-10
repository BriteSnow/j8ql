J8QL
=====
#### J8SQL is a lightweight SQL centric ORM  for Java 8

- **SQL Centric:** Making SQL Java amd OO friendly by providing fluid and modern API to build SQL queries and make JDBC calls. Flexible and thread safe SQL CRUD Query builder, allowing to greatly simplify Java Object to/from database call and marshaling. 
- **Java 8 Centric:** Java 8 offers a great opportunity to re-think Java libraries, J8QL is part of this re-thinking for ORM.
- **Postgresql First:**  Depth before breath. Going deep in one database first and then add support for others. For example, J8QL offer seamless support for Postgresql awesome "no-sql/schema-less" capability (i.e. HSTORE datatype, see [postgesql as nosql](http://thebuild.com/presentations/pg-as-nosql-pgday-fosdem-2013.pdf)). 

**Current version:** 0.5-SNAPSHOT *(Under development, relatively robust, for postgres 9.3 only, API might change)* 

**License:** Apache v2
. Use it, fork it, or own it 

[J8QL by example](#j8ql-by-example) | [Maven Info](#maven-info) | [Why not Hibernate?](#why-not-hibernate) | [Why J8QL?](#why-j8ql)

## J8sql by example

#### The Basics: <small>_Wrapping JDBC to make SQL Java 8 Friendly_</small>

```java
// Create an immutable and threadsafe DB Object from a JDBC Datasource (1 db == 1 JDBC datasource/pool)
DB db = new DBBuilder().build(dataSource);

// IMPORTANT NOTE: 
//    At DB creation, J8QL will scan the database table and column names to build its
//    internal schema dictionary. This greatly reduce redundancy between database schema and 
//    Java code, but you have to be ok with it. Its performance impact is close to none as it does it 
//    only once per DB instance creation.

// ------ simple SQL execute ------ //
try (Runner runner = db.openRunner()){ // (1 runner == 1 database connection)
    // execute a simple prepared statement 
    runner.execute("insert into \"user\" (id, username, since) values (?,?,?)",12L,"johnd",1997);

    // get back the user as a Map
    Map johndMap = runner.list("select * from \"user\" where id = ?",12L).get(0);

    // get back as a User object
    User johndUser = runner.list(User.class,"select * from \"user\" where id = ?",12L).get(0);

    // or using Stream API
    try (Stream<User> stream = runner.stream(User.class,"select * from \"user\" where id = ?",12L)){
        johndUser = stream.findFirst().get();
    } // stream will be closed, which will close the enclosing PreparedStatement

} // J8QL runner will be closed as well as the enclosing DB connection
// ------ /simple SQL execute ------ //

// ------ Query Builder Examples ------ //
try (Runner runner = db.openRunner()){ 
    
    // Simple Insert Query via columns and values
    InsertQuery<Integer> insertJohn = Query.insert("user").columns("id", "username", "since")
                                        .values(12L, "john", 1997);
    // execute the insert (by default, return the numOfRowChanged -same as PreprateStatement.executeUpdate)
    int numOfRowChanged = runner.execute(insertJohn);
    assertEquals(numOfRowChanged, 1);

    // mapOf is an static utility to create name/value hashmap
    Map jenMap = mapOf("id",13L,"username","jen","since",2004);

    // Insert a Map or Pojo object, and returning the table PK as type Long
    InsertQuery<Long> insertJen = Query.insert("user").value(jenMap).returningIdAs(Long.class);
    // execute the insert
    Long jenId = runner.execute(insertJen);
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
    // Note: where clause name can have a convenient ",_OPERATOR_"
    UpdateQuery<Integer> updateTo21stCentury = Query.update(User.class).columns("since").values(2000);
    int numOfUpdatedUsers = runner.execute(updateTo21stCentury.where("since,<",2000));
    assertEquals(1,numOfUpdatedUsers);

    // Using a SelectQuery to count
    // Note: t\This will do the appropriate select count... with the query info
    long numOf21stCenturyUsers = runner.count(Query.select("user").where("since,>=",2000));
    assertEquals(2,numOf21stCenturyUsers);
}
// ------ /Query Builder Examples------ //
```

### Limitations & Roadmap

1. Today column and table names match to Pojo properties and class names are one-one case insensitive match. Obviously, a @Table(tableName) and @Column(columnName) is coming soon.
1. Probably will move the ```Runner::execute(...Query)``` to ```Runner:exec(...Query)``` to make it less confusing with the raw select ```Runner::execute(sql...)``` and ```Runner::executeUpdate(sql...)``` return values. 
1. More advanced type coercion between type using Jomni mapper. Right now, we limit "advanced" type coercion to Enum and Object/Map to HSTORE, however, we should make it more generic. The question, is what is the cost of doing a ```preparedStatement.get...MetaData()``` if it just in memory lookup, then, we can do it for all query, if it has to go back, then, we need to think a little bit more. 

### Other advanced stuff

```java

// Join (select a ticket with its project as projectName)
SelectQuery<Record> selectBuilder = select().from("ticket").leftJoin("project", "id", "ticket", "projectId");
selectBuilder = selectBuilder.columns("ticket.*","project.name projectName");
selectBuilder = selectBuilder.whereId(0);
Optional<Record> rec = runner.first(selectBuilder);
Record record = rec.get();
// project name for ticket 0 is project 0.
assertEquals(projects[0][1],rec.get().get("projectName"));

// execute on a String SQL return the boolean returned by the preparedStatement call
// NOTE: we might change that to match the .execute of a Query. P
boolean b = runner.execute("insert into contact (id,name) values (?,?)", 1,"Mike");

// executeUpdate returns the number of row changed
int r = runner.executeUpdate("insert into contact (id,name) values (?,?)", 2,"Angie");

//  can use the sql returning in pure SQL too
Long jenId = (Long) runner.executeWithReturn("insert into contact (id,name) values (?,?) returning id", 3,"Jen").

```


## Maven Info 

Just add the following dependency in your pom.xml (and the respository below)
```xml
    <dependency>
      <groupId>org.j8ql</groupId>
      <artifactId>j8ql</artifactId>
      <version>0.5.0-SNAPSHOT</version>
    </dependency>   
```

For now, J8QL is not yet on maven central (will be soon), so add the following repository to your pom.xml.

```xml
<repositories>  
    <!-- for j8ql -->
    <repository>
        <id>BriteSnow Releases</id>
        <url>http://nexus.britesnow.com/nexus/content/repositories/releases/</url>
    </repository>
    <repository>
        <id>BriteSnow Snapshots</id>
        <url>http://nexus.britesnow.com/nexus/content/repositories/snapshots/</url>
    </repository>   
    <!-- /for j8ql -->
</repositories>

```

## Why Not Hibernate?

Hibernate is a great full featured ORM library that provides all kind of features, from cross database support, full OO semantic (i.e. collections and relationship), multi level caching, to an high level Object oriented query Language (HQL). We still use it a lot for some of our clients, especially the ones in MySQL and Oracle. 

However, the catch, and there are always a catch in any technology, is that Hibernate heavy SQL abstraction come with the following costs:

1. First, Hibernate heavy SQL abstraction can make simple SQL things much harder to do and developers can endup spending too much time learning how the higher level abstraction translate to lower SQL calls, rather than just learning the language of their database, SQL. **Data is gold** and your **Schema is data's insurance**, consequently, developers should not be afraid of learning the first language of their database. Even the Non-SQL database are now adding SQL!
2. Second, Hibernate static model can make dynamic data representation, which is widely useful in REST/JSON based applications, virtually impossible or highly unoptimized. 
3. And last but not least, I think Hibernate tries solve too many problems, and while it might looks attractive at first, developers that use 100% of hibernate often learn the hard way that some function would have been much better out of their ORM layer (e.g., level 2 caching is a good example). 

So, this is where where J8QL comes in.

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


