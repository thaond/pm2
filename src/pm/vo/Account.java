package pm.vo;

/**
 * Date: Nov 26, 2006
 * Time: 12:57:36 PM
 */
public abstract class Account {

    protected int id;
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
