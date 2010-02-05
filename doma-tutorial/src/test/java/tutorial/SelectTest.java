package tutorial;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.seasar.doma.jdbc.IterationCallback;
import org.seasar.doma.jdbc.IterationContext;
import org.seasar.doma.jdbc.SelectOptions;

import tutorial.dao.EmployeeDao;
import tutorial.dao.EmployeeDaoImpl;
import tutorial.domain.Salary;
import tutorial.entity.Employee;

public class SelectTest extends TutorialTestCase {

    private final EmployeeDao dao = new EmployeeDaoImpl();

    public void testSimpleSelect() {
        Employee employee = dao.selectById(1);
        assertNotNull(employee);
    }

    public void testConditinalSelect() {
        List<Employee> list = dao.selectByAgeRange(30, 40);
        list = dao.selectByAgeRange(30, null);
        assertEquals(12, list.size());
        list = dao.selectByAgeRange(null, 40);
        assertEquals(8, list.size());
        list = dao.selectByAgeRange(null, null);
        assertEquals(14, list.size());
    }

    public void testLoopSelect() {
        List<Integer> ages = Arrays.asList(30, 40, 50, 60);
        List<Employee> list = dao.selectByAges(ages);
        assertEquals(3, list.size());
    }

    public void testLikePredicate_prefix() throws Exception {
        List<Employee> list = dao.selectByNameWithPrefixMatching("S");
        assertEquals(2, list.size());
    }

    public void testLikePredicate_suffix() throws Exception {
        List<Employee> list = dao.selectByNameWithSuffixMatching("S");
        assertEquals(3, list.size());
    }

    public void testLikePredicate_inside() throws Exception {
        List<Employee> list = dao.selectByNameWithInsideMatching("A");
        assertEquals(7, list.size());
    }

    public void testInPredicate() throws Exception {
        List<String> names = Arrays.asList("JONES", "SCOTT", "XXX");
        List<Employee> list = dao.selectByNames(names);
        assertEquals(2, list.size());
    }

    public void testSelectByTimestampRange() throws Exception {
        Timestamp from = Timestamp.valueOf("2008-01-20 12:34:56");
        Timestamp to = Timestamp.valueOf("2008-03-20 12:34:56");
        List<Employee> list = dao.selectByHiredateRange(from, to);
        assertEquals(3, list.size());
    }

    public void testSelectByDomain() throws Exception {
        List<Employee> list = dao.selectBySalary(new Salary(2900));
        assertEquals(4, list.size());
    }

    public void testSelectDomain() throws Exception {
        Salary salary = dao.selectSummedSalary();
        assertNotNull(salary);
    }

    public void testSelectByEntity() throws Exception {
        Employee e = new Employee();
        e.setName("SMITH");
        List<Employee> list = dao.selectByExample(e);
        assertEquals(1, list.size());
    }

    public void testIterate() throws Exception {
        Salary sum = dao.selectByAge(30,
                new IterationCallback<Salary, Employee>() {

                    private Salary sum = new Salary(0);

                    @Override
                    public Salary iterate(Employee target,
                            IterationContext context) {
                        Salary salary = target.getSalary();
                        if (salary != null) {
                            sum = sum.add(salary);
                        }
                        return sum;
                    }
                });
        assertEquals(new Integer(21975), sum.getValue());
    }

    public void testIterate_exit() throws Exception {
        Salary sum = dao.selectByAge(30,
                new IterationCallback<Salary, Employee>() {

                    private Salary sum = new Salary(0);

                    @Override
                    public Salary iterate(Employee target,
                            IterationContext context) {
                        Salary salary = target.getSalary();
                        if (salary != null) {
                            sum = sum.add(salary);
                        }
                        if (sum.getValue() != null && sum.getValue() > 10000) {
                            context.exit();
                        }
                        return sum;
                    }
                });
        assertEquals(new Integer(10725), sum.getValue());
    }

    public void testOffsetLimit() throws Exception {
        SelectOptions options = SelectOptions.get().offset(5).limit(3);
        List<Employee> list = dao.selectAll(options);
        assertEquals(3, list.size());
    }

    public void testCount() throws Exception {
        SelectOptions options = SelectOptions.get().offset(5).limit(3).count();
        List<Employee> list = dao.selectAll(options);
        assertEquals(3, list.size());
        assertEquals(14, options.getCount());
    }
}