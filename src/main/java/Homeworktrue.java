import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Перенести структуру дз третьего урока на JPA:
 * 1. Описать сущности Student и Group
 * 2. Написать запросы: Find, Persist, Remove
 * 3. ... поупражняться с разными запросами ...
 */
public class Homeworktrue {
    public static void main(String[] args) throws SQLException {

        //Конектимся к базе H2
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
            prepareTables(connection);
            run(connection);
        }
    }

    // Создаем таблицы заполняем значениями
    private static void prepareTables(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
        create table groups(
          id bigint not null auto_increment,
          name varchar(256) NOT NULL,
          primary key (id)
        )
        """);
        }

        try (Statement st = connection.createStatement()) {
            st.execute("""
        create table students(
          id bigint not null auto_increment,
          firstname varchar(256) NOT NULL,
          secondname varchar(256) NOT NULL,
          groupname VARCHAR(128) NOT NULL,
          primary key (id)
        )
        """);
        }

        try (Statement st = connection.createStatement()) {
            st.execute("""
            insert into students(id, firstName, secondName, groupName) values
               (1, 'Igor', 'Petrov', 'group#1'),
               (2, 'Ivan', 'Sidorov', 'group#2'),
               (3, 'Jon', 'Smit', 'group#3'),
               (4, 'Mari', 'Sidorova', 'group#2'),
               (5, 'Rima', 'Petrova', 'group#3'),
               (6, 'Tany', 'Istomina', 'group#1'),
               (7, 'Ivan', 'Tretiakov', 'group#3'),
               (8, 'Petr', 'Sidorovich', 'group#3'),
               (9, 'Jonatan', 'Praim', 'group#2')                            
            """);
        }

        try (Statement st = connection.createStatement()) {
            st.execute("""
            insert into groups(id, name) values
               (1, 'group#1'),
               (2, 'group#2'),
               (3, 'group#3')                            
            """);
        }

    }

    private static void run(Connection connection) throws SQLException {
        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            try(Session session = sessionFactory.openSession()){
                Student students = session.find(Student.class, 1L);
                System.out.println(students);
            }

            Student newStudent = new Student();
            newStudent.setId(10L);
            newStudent.setFirstname("Anton");
            newStudent.setSecondname("Rudenko");
            newStudent.setGroupname("group#2");
            System.out.println(newStudent);
            //persist
            try(Session session = sessionFactory.openSession()){
                Transaction tx = session.beginTransaction();
                session.persist(newStudent);
                tx.commit();
            }

            //merge
            try(Session session = sessionFactory.openSession()){
                newStudent.setFirstname("Yakov");
                Transaction tx = session.beginTransaction();
                session.merge(newStudent);
                tx.commit();
            }

            //remove
            try(Session session = sessionFactory.openSession()){
                Transaction tx = session.beginTransaction();
                session.remove(newStudent);
                tx.commit();
            }

            //find
            try(Session session = sessionFactory.openSession()){
                Student students = session.find(Student.class, 2L);
                System.out.println(students);
            }
        }
    }
}
