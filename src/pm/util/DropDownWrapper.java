package pm.util;

import pm.vo.Account;

/**
 * Date: Nov 26, 2006
 * Time: 1:11:19 PM
 */
public class DropDownWrapper {

    private Account account;

    public static DropDownWrapper All = new DropDownWrapper(new AllAccount());


    public DropDownWrapper(Account account) {
        this.account = account;
    }


    public Account getAccount() {
        return account;
    }


    public String toString() {
        return account.getName();
    }

    static class AllAccount extends Account {

        public int getId() {
            return -1;
        }

        public String getName() {
            return "All";
        }
    }
}
