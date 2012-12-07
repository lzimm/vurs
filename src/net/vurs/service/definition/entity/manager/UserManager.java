package net.vurs.service.definition.entity.manager;

/*
import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.entity.definition.User;

public class UserManager extends CassandraBackedEntityManager<User> {
	
}
*/

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.entity.definition.User;

public class UserManager extends SQLBackedEntityManager<User> {
	
}