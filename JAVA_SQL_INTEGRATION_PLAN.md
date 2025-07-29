# Java-SQL Integration Plan for Davon Library Management System

## Overview
This document outlines the plan for integrating the SQL database schema with the existing Java backend using Quarkus, Hibernate ORM, and Panache repositories.

## Current State Analysis

### Existing Configuration
- **Framework**: Quarkus 3.24.3
- **ORM**: Hibernate ORM with Panache
- **Database**: H2 in-memory (development)
- **Generation Strategy**: `drop-and-create` (development)

### Current Java Entities
The existing Java entities are well-structured and align with the SQL schema:
- ✅ User (with Member, Librarian, Guest inheritance)
- ✅ Book
- ✅ Author  
- ✅ Category
- ✅ Loan
- ✅ Reservation
- ✅ Fine
- ✅ Address
- ✅ Membership

## Integration Steps

### Phase 1: Database Configuration Updates

#### 1.1 Update application.properties
```properties
# Production Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=${DB_USERNAME:library_user}
quarkus.datasource.password=${DB_PASSWORD:library_password}
quarkus.datasource.jdbc.url=${DB_URL:jdbc:postgresql://localhost:5432/library_db}

# Development Database Configuration
%dev.quarkus.datasource.db-kind=h2
%dev.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
%dev.quarkus.h2.console.enabled=true

# Test Database Configuration
%test.quarkus.datasource.db-kind=h2
%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1

# Hibernate Configuration
quarkus.hibernate-orm.database.generation=validate
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.sql-load-script=import.sql

# Development only - use drop-and-create
%dev.quarkus.hibernate-orm.database.generation=drop-and-create
%test.quarkus.hibernate-orm.database.generation=drop-and-create
```

#### 1.2 Add Database Dependencies
Update `pom.xml` to include PostgreSQL driver:

```xml
<dependencies>
    <!-- Keep existing H2 for development -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-jdbc-h2</artifactId>
    </dependency>
    
    <!-- Add PostgreSQL for production -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-jdbc-postgresql</artifactId>
    </dependency>
    
    <!-- Add Flyway for database migrations -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-flyway</artifactId>
    </dependency>
</dependencies>
```

### Phase 2: Database Migration Setup

#### 2.1 Create Migration Scripts Directory
```bash
mkdir -p src/main/resources/db/migration
```

#### 2.2 Create Initial Migration Script
`src/main/resources/db/migration/V1.0.0__Initial_Schema.sql`
```sql
-- Copy the complete schema.sql content here
-- This becomes the baseline migration
```

#### 2.3 Create Sample Data Migration (Optional)
`src/main/resources/db/migration/V1.0.1__Sample_Data.sql`
```sql
-- Copy the sample_data.sql content here
-- Only for development/testing environments
```

#### 2.4 Configure Flyway
Add to `application.properties`:
```properties
# Flyway configuration
quarkus.flyway.migrate-at-start=true
quarkus.flyway.baseline-on-migrate=true
quarkus.flyway.locations=db/migration

# Only run sample data in dev/test
%dev.quarkus.flyway.locations=db/migration,db/dev
%test.quarkus.flyway.locations=db/migration,db/test
```

### Phase 3: Entity Validation and Updates

#### 3.1 Review and Update Existing Entities

**User Entity Updates** (if needed):
```java
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    // ... other fields match SQL schema exactly
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false)
    private UserRole role;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Membership> memberships;
    
    // ... other relationships
}
```

**Book Entity Validation**:
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(unique = true)
    private String isbn;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    // Ensure all fields match SQL schema
}
```

#### 3.2 Add Missing Validation Annotations
```java
@Entity
public class Loan {
    // Add validation that matches SQL constraints
    @AssertTrue(message = "Due date must be after borrow date")
    private boolean isDueDateValid() {
        return dueDate == null || borrowDate == null || dueDate.isAfter(borrowDate);
    }
    
    @AssertTrue(message = "Return date must be after borrow date")
    private boolean isReturnDateValid() {
        return returnDate == null || borrowDate == null || 
               !returnDate.isBefore(borrowDate);
    }
}
```

### Phase 4: Repository Enhancements

#### 4.1 Add Custom Query Methods
Enhance existing repositories with SQL-optimized queries:

```java
@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    
    // Use the views created in SQL schema
    @Query("SELECT b FROM v_available_books b WHERE b.title LIKE :title")
    public List<Book> findAvailableBooksByTitle(@Param("title") String title);
    
    // Leverage database indexes
    @Query("SELECT b FROM Book b WHERE b.status = :status ORDER BY b.title")
    public List<Book> findByStatus(@Param("status") BookStatus status);
    
    // Use JOIN FETCH for performance
    @Query("SELECT b FROM Book b " +
           "JOIN FETCH b.author a " +
           "JOIN FETCH b.category c " +
           "WHERE b.status = 'AVAILABLE'")
    public List<Book> findAvailableBooksWithDetails();
    
    // Native queries for complex reports
    @Query(value = "SELECT * FROM v_popular_books LIMIT :limit", nativeQuery = true)
    public List<Object[]> findMostPopularBooks(@Param("limit") int limit);
}
```

#### 4.2 Add Loan Repository Methods
```java
@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {
    
    public List<Loan> findOverdueLoans() {
        return find("status = 'BORROWED' AND dueDate < CURRENT_DATE").list();
    }
    
    public List<Loan> findLoansDueSoon(int days) {
        return find("status = 'BORROWED' AND dueDate BETWEEN CURRENT_DATE AND ?1",
                   LocalDate.now().plusDays(days)).list();
    }
    
    @Query("SELECT l FROM Loan l " +
           "JOIN FETCH l.user u " +
           "JOIN FETCH l.book b " +
           "JOIN FETCH b.author a " +
           "WHERE u.id = :userId " +
           "ORDER BY l.borrowDate DESC")
    public List<Loan> findUserLoanHistory(@Param("userId") Long userId);
}
```

### Phase 5: Service Layer Integration

#### 5.1 Create Report Services
```java
@ApplicationScoped
public class ReportService {
    
    @Inject
    EntityManager em;
    
    public List<OverdueBooksReport> getOverdueBooksReport() {
        String sql = """
            SELECT l.id as loanId, 
                   CONCAT(u.first_name, ' ', u.last_name) as borrowerName,
                   u.email, b.title, l.due_date, 
                   DATEDIFF(CURRENT_DATE, l.due_date) as daysOverdue
            FROM loans l 
            JOIN users u ON l.user_id = u.id 
            JOIN books b ON l.book_id = b.id 
            WHERE l.due_date < CURRENT_DATE AND l.status = 'BORROWED'
            ORDER BY daysOverdue DESC
            """;
        
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> new OverdueBooksReport(
                (Long) row[0], (String) row[1], (String) row[2],
                (String) row[3], (Date) row[4], (Integer) row[5]))
            .toList();
    }
    
    public DatabaseHealthCheck getHealthCheck() {
        // Use the health check query from queries_and_reports.sql
        String sql = """
            SELECT 'Total Users' as metric, COUNT(*) as count FROM users
            UNION ALL SELECT 'Available Books', COUNT(*) FROM books WHERE status = 'AVAILABLE'
            UNION ALL SELECT 'Active Loans', COUNT(*) FROM loans WHERE status IN ('BORROWED', 'OVERDUE')
            -- ... rest of health check query
            """;
        
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        return new DatabaseHealthCheck(results);
    }
}
```

#### 5.2 Update Business Logic Services
```java
@ApplicationScoped
public class LoanService {
    
    @Inject
    LoanRepository loanRepository;
    
    @Inject
    BookRepository bookRepository;
    
    @Transactional
    public Loan borrowBook(Long userId, Long bookId, int loanPeriodDays) {
        Book book = bookRepository.findById(bookId);
        
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessException("Book is not available for borrowing");
        }
        
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        loan.setStatus(LoanStatus.BORROWED);
        
        // The trigger will automatically update book status
        loanRepository.persist(loan);
        
        return loan;
    }
    
    @Transactional
    public void returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        
        // The trigger will automatically update book status to AVAILABLE
        loanRepository.persist(loan);
    }
}
```

### Phase 6: Testing Strategy

#### 6.1 Unit Tests with Test Database
```java
@QuarkusTest
@TestProfile(H2TestProfile.class)
class BookRepositoryTest {
    
    @Inject
    BookRepository bookRepository;
    
    @Test
    @Transactional
    void testFindAvailableBooks() {
        // Test against H2 database with test data
        List<Book> available = bookRepository.findByStatus(BookStatus.AVAILABLE);
        assertThat(available).isNotEmpty();
    }
}
```

#### 6.2 Integration Tests
```java
@QuarkusTest
@TestProfile(PostgreSQLTestProfile.class)
class LoanServiceIntegrationTest {
    
    @Inject
    LoanService loanService;
    
    @Test
    @Transactional
    void testBorrowBookUpdatesStatus() {
        Loan loan = loanService.borrowBook(1L, 1L, 14);
        
        // Verify trigger worked
        Book book = bookRepository.findById(1L);
        assertThat(book.getStatus()).isEqualTo(BookStatus.BORROWED);
    }
}
```

### Phase 7: Performance Monitoring

#### 7.1 Add Query Performance Logging
```properties
# Enable slow query logging
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.log.bind-parameters=true
quarkus.hibernate-orm.statistics=true

# Log slow queries (> 2 seconds)
quarkus.log.category."org.hibernate.SQL".level=DEBUG
quarkus.log.category."org.hibernate.type.descriptor.sql.BasicBinder".level=TRACE
```

#### 7.2 Add Health Checks
```java
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {
    
    @Inject
    EntityManager em;
    
    @Override
    public HealthCheckResponse call() {
        try {
            // Use the health check query
            Query query = em.createNativeQuery("SELECT COUNT(*) FROM users");
            Long userCount = (Long) query.getSingleResult();
            
            return HealthCheckResponse.up("database")
                .withData("userCount", userCount)
                .build();
        } catch (Exception e) {
            return HealthCheckResponse.down("database")
                .withData("error", e.getMessage())
                .build();
        }
    }
}
```

## Deployment Strategy

### Development Environment
1. **Database**: H2 in-memory with `drop-and-create`
2. **Data**: Auto-loaded from `import.sql`
3. **Migrations**: Not used (schema auto-generated)

### Testing Environment
1. **Database**: H2 or PostgreSQL testcontainer
2. **Data**: Test-specific datasets
3. **Migrations**: Full migration suite

### Production Environment
1. **Database**: PostgreSQL with connection pooling
2. **Data**: Migrated from existing system or clean start
3. **Migrations**: Flyway-managed with version control
4. **Monitoring**: Full query performance monitoring

## Migration Checklist

### Pre-Migration
- [ ] Backup existing data (if any)
- [ ] Test all queries in queries_and_reports.sql
- [ ] Validate all entity mappings
- [ ] Run complete test suite
- [ ] Performance test critical queries

### Migration Steps
- [ ] Update application.properties
- [ ] Add PostgreSQL dependency
- [ ] Create migration scripts
- [ ] Update entities if needed
- [ ] Enhance repositories
- [ ] Test database triggers
- [ ] Validate constraints
- [ ] Performance test

### Post-Migration
- [ ] Monitor query performance
- [ ] Verify triggers work correctly
- [ ] Check constraint enforcement
- [ ] Validate report generation
- [ ] Monitor system health

## Troubleshooting Guide

### Common Issues

#### H2 vs PostgreSQL Compatibility
- **Date Functions**: Use `CURRENT_DATE` instead of `NOW()`
- **String Concatenation**: Use `CONCAT()` function
- **Case Sensitivity**: PostgreSQL identifiers are case-sensitive

#### Performance Issues
- **Missing Indexes**: Verify all indexes from schema.sql are created
- **N+1 Queries**: Use `JOIN FETCH` in entity queries
- **Large Result Sets**: Implement pagination

#### Migration Problems
- **Schema Differences**: Use Flyway validate to check consistency
- **Data Type Mismatches**: Verify enum mappings
- **Constraint Violations**: Check existing data before applying constraints

---

## Summary

This integration plan provides a structured approach to connecting your well-designed SQL schema with the existing Java backend. The key benefits include:

1. **Data Integrity**: Database constraints and triggers ensure business rules
2. **Performance**: Optimized indexes and views for common operations  
3. **Maintainability**: Clear separation between schema and application logic
4. **Scalability**: Production-ready PostgreSQL setup with monitoring

The phased approach allows for incremental implementation and testing, minimizing risk during the integration process. 