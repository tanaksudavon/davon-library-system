# 🐛 **DEBUGGING CHALLENGE: New Library Features**

## 📋 **Overview**
I've implemented 4 new features with **deliberate bugs** for you to debug. Each service contains multiple types of bugs to practice different debugging skills.

## 🎯 **New Features Created:**

### **1. Fine Calculation System** (`FineCalculationService.java`)
**REST Endpoints:** `/api/fines/*`

**🐛 Bugs to Find:**
1. **Null Pointer Exception** - No null check for `loan.getDueDate()`
2. **Logic Error** - Wrong date comparison (`dueDate.isAfter(today)` should be `today.isAfter(dueDate)`)
3. **Missing Validation** - Doesn't check if fine already exists for loan
4. **Calculation Bug** - No validation for negative overdue days
5. **BigDecimal Comparison** - Fixed but was using `>` instead of `compareTo()`
6. **Always Returns Null** - `findExistingFine()` method always returns null
7. **Null Handling** - No null check for `userFines` list
8. **Null Pointer** - No null check for `fine.getAmount()`
9. **Parameter Validation** - No null check for `fineId` in `payFine()`
10. **Null Pointer** - No null check for `fine` object before using

### **2. Book Reservation System** (`ReservationService.java`)
**REST Endpoints:** `/api/reservations/*`

**🐛 Bugs to Find:**
1. **Null Validation** - No null checks for parameters
2. **Null Pointer** - No null check for book object
3. **Duplicate Check Missing** - No check for existing active reservations
4. **Date Logic Error** - Expiration date is `plusDays(-7)` instead of `plusDays(7)`
5. **Status Update Missing** - Doesn't set book status to RESERVED
6. **Authorization Bug** - Any user can cancel any reservation
7. **Null Check Missing** - No null check for reservation object
8. **Book Status Bug** - Doesn't free up book when canceling reservation
9. **Status Validation** - No validation of current status before fulfilling
10. **Filter Bug** - `getUserActiveReservations()` returns ALL reservations, not just active
11. **Date Comparison** - Wrong comparison in `expireOldReservations()`
12. **Book Status Bug** - Forgets to free up book when expiring reservations
13. **Sorting Missing** - Reservation queue not sorted by date
14. **Logic Error** - Wrong condition for reservation limits
15. **Duplicate Check** - Doesn't check if user already reserved specific book

### **3. User Notification System** (`NotificationService.java`)
**No REST endpoints created - internal service**

**🐛 Bugs to Find:**
1. **Null Pointer** - No null checks for parameters
2. **Null Pointer** - No null check for user object
3. **Optional Handling** - Direct `.get()` call without checking if present
4. **Date Formatting** - Potential null pointer with date formatting
5. **String Handling** - Poor string concatenation practices
6. **Return Logic** - Always returns success even if notification fails
7. **Error Handling** - No error handling for repository calls
8. **Null Pointer** - Null pointer if user has no first name
9. **Type Assumption** - Assumes `getTotalFinesForUser` never returns null
10. **Logic Error** - Wrong condition (`totalFines < 0` instead of `> 0`)
11. **Performance** - No pagination for large user lists
12. **Null Handling** - Doesn't handle null email addresses
13. **Rate Limiting** - No rate limiting for bulk emails
14. **Hardcoded Value** - Returns hardcoded fine amount
15. **No Implementation** - No actual email sending
16. **Validation Missing** - No email format validation
17. **Success Logic** - Always returns "SUCCESS" even for null emails
18. **Inconsistent Types** - Returns boolean vs String inconsistency
19. **Wrong Condition** - Returns true for "FAILED" instead of "SUCCESS"
20. **Default Values** - Always returns default preferences

### **4. Report Generation System** (`ReportGenerationService.java`)
**No REST endpoints created - internal service**

**🐛 Bugs to Find:**
1. **Date Range Bug** - Will fail for February (day 31 doesn't exist)
2. **No Filtering** - Gets all loans instead of filtering by date
3. **Division by Zero** - No check for empty loan list
4. **Logic Error** - Incorrect overdue calculation (missing return check)
5. **Memory Leak** - Loads all data into memory at once
6. **Calculation Error** - Wrong active loans calculation
7. **Null Pointer** - Potential null pointer with empty fines list
8. **Sorting Error** - Wrong sorting order (ascending instead of descending)
9. **Logic Error** - Wrong date comparison for overdue books
10. **Validation Missing** - No null checks for book/user data
11. **Negative Values** - Potential negative days overdue
12. **Performance** - N+1 query problem
13. **Sorting Error** - Wrong sorting order for popularity
14. **Missing Method** - Undefined `calculatePopularityScore` method (fixed)
15. **Filter Missing** - No date filtering for financial report
16. **Status Error** - Wrong status check (UNPAID instead of PAID)
17. **Calculation Error** - Wrong outstanding calculation (add instead of subtract)
18. **CSV Escaping** - No proper CSV escaping for commas in data
19. **Hardcoded Values** - Hardcoded delimiters instead of proper CSV formatting

## 🎯 **Your Debugging Challenge:**

### **Step 1: Compile and Test**
```bash
mvn compile
mvn test
```

### **Step 2: Start Application**
```bash
mvn quarkus:dev
```

### **Step 3: Test REST Endpoints**
```bash
# Test Fine Calculation
curl -X POST http://localhost:8081/api/fines/calculate-overdue

# Test Reservations  
curl -X POST http://localhost:8081/api/reservations/book/1/user/1

# Test User Fines
curl http://localhost:8081/api/fines/user/1/total
```

### **Step 4: Debug Issues**
1. **Set breakpoints** in the new service methods
2. **Watch variables** to see incorrect values
3. **Step through logic** to find errors
4. **Fix bugs one by one**
5. **Test after each fix**

## 🏆 **Debugging Skills You'll Practice:**

- ✅ **Null Pointer Exception handling**
- ✅ **Logic error identification**
- ✅ **Date/Time calculation bugs**
- ✅ **BigDecimal comparison issues**
- ✅ **Collection handling errors**
- ✅ **Status validation problems**
- ✅ **Performance issues (N+1 queries)**
- ✅ **Data filtering bugs**
- ✅ **Authorization/security issues**
- ✅ **Type conversion problems**

## 🎯 **Success Criteria:**
- All services compile without errors ✅ (Done)
- All unit tests pass (you'll need to create tests)
- REST endpoints work correctly
- No null pointer exceptions
- Correct business logic implementation
- Proper error handling

**Good luck debugging! This is excellent practice for real-world debugging scenarios.** 🚀 