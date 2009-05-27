package pm.util.enumlist;

import pm.util.PMDate;

import javax.swing.*;

/**
 * Date: Dec 11, 2006
 * Time: 8:50:48 PM
 */
public enum SyncStatus {
    InSync() {
        @Override
        public Icon getIcon() {
            return new ImageIcon(SyncStatus.class.getClassLoader().getResource("pm/ui/resource/Refresh_gray.gif"));
        }
    },
    CanSync() {
        @Override
        public Icon getIcon() {
            return new ImageIcon(SyncStatus.class.getClassLoader().getResource("pm/ui/resource/Refresh_green.gif"));
        }
    },
    MustSync() {
        @Override
        public Icon getIcon() {
            return new ImageIcon(SyncStatus.class.getClassLoader().getResource("pm/ui/resource/Refresh_red.gif"));
        }
    };

    public abstract Icon getIcon();

    public static SyncStatus state(boolean lastRunStatus, PMDate lastRunDate, PMDate nextRunDate) {
        SyncStatus state = MustSync;
        PMDate currDate = new PMDate();
        if (lastRunStatus) {
            if (lastRunDate.equals(currDate)) {
                state = SyncStatus.InSync;
            } else if (currDate.before(nextRunDate)) {
                state = SyncStatus.CanSync;
            }
        }
        return state;
    }
}
