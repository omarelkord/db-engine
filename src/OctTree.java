import java.io.Serializable;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OctTree implements Serializable {
    int capacity;
    Cube boundary;
    boolean divided;

    Vector<Point> items;
    OctTree[] children;

    public OctTree(int capacity, Cube boundary) {
        this.capacity = capacity;
        this.boundary = boundary;
        this.items = new Vector<>();

    }


    public Vector<Point> searchPoint(Object x, Object y, Object z) {
        Object front, back, right, left, bottom, top = null;
        if (x == null) {
            front = this.boundary.front;
            back = this.boundary.back;
        } else
            front = back = x;

        if (y == null) {
            right = this.boundary.right;
            left = this.boundary.left;
        } else
            right = left = y;

        if (z == null) {
            bottom = this.boundary.bottom;
            top = this.boundary.top;
        } else
            bottom = top = z;

        Cube bound = new Cube(front, back, left, right, top, bottom);
        //System.out.print(bound.front+" "+bound.back);
        return searchWithboundary(bound);

    }


    public Vector<Point> searchWithboundary(Cube bound) {
        Vector<Point> foundPoints = new Vector<>();
        if (!boundary.intersects(bound)) {
            return foundPoints;
        }
        if (!divided) {
            for (Point item : items) {
                if (Cube.checkPointsInboundary(item, bound)) {
                    //System.out.println(item);
                    foundPoints.add(item);
                }
            }
        } else
            for (OctTree child : children) {

                foundPoints.addAll(child.searchWithboundary(bound));
            }


        return foundPoints;
    }

    public boolean insertInTree(Point point) {
        if (!boundary.isInRange((Comparable) point.x, (Comparable) this.boundary.front, (Comparable) this.boundary.back))
            return false;
        if (!boundary.isInRange((Comparable) point.y, (Comparable) this.boundary.right, (Comparable) this.boundary.left))
            return false;
        if (!boundary.isInRange((Comparable) point.z, (Comparable) this.boundary.top, (Comparable) this.boundary.bottom))
            return false;


        if (!divided) {
            if (items.size() < this.capacity) {
                items.add(point);
                return true;
             }
             else
             divide();}
         for(OctTree c : this.children) {
             if(c.insertInTree(point))
                 return true;
         }
         return false;
    }

    public void divide() {

        Cube child1 = new Cube(boundary.front, boundary.centre.x, boundary.left, boundary.centre.y, boundary.top, boundary.centre.z);

        Cube child2 = new Cube(boundary.front, boundary.centre.x, boundary.centre.y, boundary.right, boundary.top, boundary.centre.z);

        Cube child3 = new Cube(boundary.front, boundary.centre.x, boundary.left, boundary.centre.y, boundary.centre.z, boundary.bottom);

        Cube child4 = new Cube(boundary.front, boundary.centre.x, boundary.centre.y, boundary.right, boundary.centre.z, boundary.bottom);

        Cube child5 = new Cube(boundary.centre.x, boundary.back, boundary.left, boundary.centre.y, boundary.top, boundary.centre.z);

        Cube child6 = new Cube(boundary.centre.x, boundary.back, boundary.centre.y, boundary.right, boundary.top, boundary.centre.z);

        Cube child7 = new Cube(boundary.centre.x, boundary.back, boundary.left, boundary.centre.y, boundary.centre.z, boundary.bottom);

        Cube child8 = new Cube(boundary.centre.x, boundary.back, boundary.centre.y, boundary.right, boundary.centre.z, boundary.bottom);

        children = new OctTree[8];
        children[0] = new OctTree(this.capacity, child1);
        children[1] = new OctTree(this.capacity, child2);
        children[2] = new OctTree(this.capacity, child3);
        children[3] = new OctTree(this.capacity, child4);
        children[4] = new OctTree(this.capacity, child5);
        children[5] = new OctTree(this.capacity, child6);
        children[6] = new OctTree(this.capacity, child7);
        children[7] = new OctTree(this.capacity, child8);

//        System.out.println(child1.centre);
//        System.out.println(child2.centre);
//        System.out.println(child3.centre);
//        System.out.println(child4.centre);
//        System.out.println(child5.centre);
//        System.out.println(child6.centre);
//        System.out.println(child7.centre+ " "+child7.front+" "+child7.back+" "+child7.left+" "+child7.right+" "+child7.top);
//        System.out.println(child8.centre);


        for (int i = 0; i < items.size(); i++) {
            //System.out.println(items.get(i));
            for (OctTree child : children) {
                Cube boundary = child.boundary;
                Point item = items.get(i);
                if (boundary.isInRange((Comparable) item.x, (Comparable) child.boundary.front, (Comparable) child.boundary.back)) {
                    if (boundary.isInRange((Comparable) item.y, (Comparable) child.boundary.right, (Comparable) child.boundary.left)) {
                        if (boundary.isInRange((Comparable) item.z, (Comparable) child.boundary.top, (Comparable) child.boundary.bottom)) {
                            child.items.add(items.get(i));
                            break;
                        }
                    }
                }

            }
        }
        divided = true;
    }

    public boolean updateTree(Point old, Point curr) {
        deleteInTree(old.x, old.y, old.z);
        return insertInTree(curr);
    }

    public void deleteInTree(Object x, Object y, Object z) {
        Object front, back, right, left, bottom, top = null;
        if (x == null) {
            front = this.boundary.front;
            back = this.boundary.back;
        } else
            front = back = x;

        if (y == null) {
            right = this.boundary.right;
            left = this.boundary.left;
        } else
            right = left = y;

        if (z == null) {
            bottom = this.boundary.bottom;
            top = this.boundary.top;
        } else
            bottom = top = z;

        Cube bound = new Cube(front, back, left, right, top, bottom);
        //System.out.print(bound.front+" "+bound.back);
        deleteWithboundary
                (bound);
    }

    public void deleteWithboundary(Cube bound) {
        if (!boundary.intersects(bound)) {
            return;
        }
        if (!divided) {

            for (int i = 0; i < items.size(); ) {
                if (this.boundary.checkPointsInboundary
                        (items.get(i), bound))
                    this.items.remove(items.get(i));

                else
                    i++;
            }
        } else
            for (OctTree child : children) {
                child.deleteWithboundary(bound);

            }


    }

    public void printTree() {
        Queue<OctTree> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            System.out.println();
            Queue<OctTree> temp = new LinkedList<>();
            while (!queue.isEmpty()) {
                OctTree node = queue.poll();
                if (node.divided) {
                    System.out.print("Node ");
                    for (OctTree child : node.children)
                        temp.add(child);
                } else
                    node.printItems();
            }

            while (!temp.isEmpty())
                queue.add(temp.poll());

        }


    }

    public void printItems() {
        if (items.isEmpty()) {
            System.out.print("{}");
        } else if (items.size() == 1) {
            System.out.print("{" + items.get(0) + "}");
        } else
            for (int i = 0; i < this.items.size(); i++) {
                if (i == items.size() - 1)
                    System.out.print(items.get(i) + "}");
                else if (i == 0)
                    System.out.print("{" + items.get(i) + "...");
                else
                    System.out.print(items.get(i) + "...");
            }
    }

    public static void main(String[] args) {
        Cube boundary
                = new Cube(100, 0, 0, 100, 100, 0);
        OctTree t = new OctTree(3, boundary
        );
        Random random = new Random();
        Point point1 = new Point(5, 5, 5, 0);
        t.insertInTree(point1);
        Point point2 = new Point(10, 10, 10, 0);
        t.insertInTree(point2);
        Point point3 = new Point(90, 20, 20, 0);
        t.insertInTree(point3);
        Point point4 = new Point(60, 20, 20, 0);
        t.insertInTree(point4);
        Point point5 = new Point(12, 12, 12, 0);
        t.insertInTree(point5);
        Point point6 = new Point(30, 30, 30, 0);
        t.insertInTree(point6);
        System.out.println(t.searchPoint(null, 20, 20));
        t.printTree();

    }
}

class Cube implements Serializable {
    Point centre;
    Object front, back, top, bottom, left, right;

    public Cube(Object front, Object back, Object left, Object right, Object top, Object bottom) {
        this.front = front;
        this.back = back;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.centre = new Point(mean(front, back), mean(right, left), mean(top, bottom), 0);
    }

    public boolean intersects(Cube boundary
    ) {
        boolean cond1 = ((Comparable) this.front).compareTo((Comparable) boundary.back) < 0;
        boolean cond2 = ((Comparable) this.back).compareTo((Comparable) boundary.front) > 0;
        boolean cond3 = ((Comparable) this.right).compareTo((Comparable) boundary.left) < 0;
        boolean cond4 = ((Comparable) this.left).compareTo((Comparable) boundary.right) > 0;
        boolean cond5 = ((Comparable) this.top).compareTo((Comparable) boundary.bottom) < 0;
        boolean cond6 = ((Comparable) this.bottom).compareTo((Comparable) boundary.top) > 0;

        return !(cond1 || cond2 || cond3 || cond4 || cond5 || cond6);
    }

    public static Object addPoints(Object x, Object value) {
        if (x instanceof Integer)
            return (int) x + (int) value;

        if (x instanceof String) {
            String s1 = (String) x;
            String s2 = (String) value;
            char c1 = s1.charAt(0);
            char c2 = s2.charAt(0);
            int result = c1 + c2;
            return (char) result;
        }
        if (x instanceof Double) {
            return (double) x + (double) value;
        }
        if (x instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) x);
            calendar.add(Calendar.MILLISECOND, (int) (((Date) value).getTime() - ((Date) x).getTime()));
            return calendar.getTime();
        }

        return null;

    }


    public static Object mean(Object upper, Object lower) {
        if (upper instanceof Integer)
            return ((Integer) upper + (Integer) lower) / 2;
        if (upper instanceof Double)
            return ((Double) upper + (Double) lower) / 2.0;
        if (upper instanceof String)
            return stringMean((String) upper, (String) lower);
        if (upper instanceof Date)
            return dateMean((Date) upper, (Date) lower);
        return null;
    }

    public static String stringMean(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        int asciiSum1 = 0;
        for (int i = 0; i < str1.length(); i++) {
            asciiSum1 += (int) str1.charAt(i);
        }
        double asciiAvg1 = (double) asciiSum1 / (double) str1.length();

        int asciiSum2 = 0;
        for (int i = 0; i < str2.length(); i++) {
            asciiSum2 += (int) str2.charAt(i);
        }
        double asciiAvg2 = (double) asciiSum2 / (double) str2.length();

        int meanAscii = (int) Math.round((asciiAvg1 + asciiAvg2) / 2.0);
        char meanChar = (char) meanAscii;
        return String.valueOf(meanChar);
    }

    public static Date dateMean(Date date1, Date date2) {
        long millisBetween = date2.getTime() - date1.getTime();
        long millisMean = date1.getTime() + (millisBetween / 2);
        Date meanDate = new Date(millisMean);
        return meanDate;
    }

    public static boolean checkPointsInboundary(Point item, Cube bound) {
        Comparable x = (Comparable) item.x;
        Comparable y = (Comparable) item.y;
        Comparable z = (Comparable) item.z;
        boolean cond1 = x.compareTo(bound.front) <= 0 && x.compareTo(bound.back) >= 0;
        boolean cond2 = y.compareTo(bound.right) <= 0 && y.compareTo(bound.left) >= 0;
        boolean cond3 = z.compareTo(bound.top) <= 0 && z.compareTo(bound.bottom) >= 0;

        return cond1 && cond2 && cond3;
    }

    public boolean isInRange(Comparable value, Comparable Upper, Comparable Lower) {

        return value.compareTo(Upper) < 0 && value.compareTo(Lower) >= 0;
    }


}

class Point implements Serializable {
    Object x, y, z;
    int pageReference;

    public Point(Object x, Object y, Object z, int r) {
        this.x = x;
        this.y = y;
        this.z = z;
        pageReference = r;
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}




