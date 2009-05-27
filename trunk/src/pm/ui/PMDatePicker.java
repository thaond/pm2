package pm.ui;

import com.michaelbaranov.microba.calendar.DatePicker;
import com.michaelbaranov.microba.calendar.HolidayPolicy;
import com.michaelbaranov.microba.calendar.VetoPolicy;
import com.michaelbaranov.microba.common.PolicyListener;
import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.util.PMDate;

import java.beans.PropertyVetoException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thiyagu
 * @version $Id: PMDatePicker.java,v 1.2 2008/01/02 11:49:12 tpalanis Exp $
 * @since 18-Dec-2007
 */
public class PMDatePicker extends DatePicker {

    private static Logger logger = Logger.getLogger(PMDatePicker.class);

    public static PMDatePicker instanceWithoutRestriction() {
        return new PMDatePicker();
    }

    public static PMDatePicker instanceWithLastQuoteDate() {
        return instanceWithRestrictedToQuoteDate(lastQuoteDate().getJavaDate());
    }

    public static PMDatePicker instanceWithLastYearQuoteDate() {
        return instanceWithRestrictedToQuoteDate(lastQuoteDate().get52WeeksBefore().getJavaDate());
    }

    public static PMDatePicker instanceWithMarketDateRestriction() {
        PMDatePicker picker = new PMDatePicker();
        PMDate pmDate = DAOManager.getDateDAO().lastWorkingDayLatestOf(new PMDate());
        picker.setDateWrapped(pmDate.getJavaDate());
        picker.setMarketHolidayPolicy();
        return picker;
    }

    private static PMDatePicker instanceWithRestrictedToQuoteDate(final Date javaDate) {
        PMDatePicker picker = new PMDatePicker();
        picker.setDateWrapped(javaDate);
        picker.setQuoteBasedHolidayPolicy();
        return picker;
    }

    private static PMDate lastQuoteDate() {
        return DAOManager.getDateDAO().getLastQuoteDate();
    }

    private void setDateWrapped(Date javaDate) {
        try {
            setDate(javaDate);
        } catch (PropertyVetoException e) {
            logger.error(e, e);
        }
    }

    private PMDatePicker() {
        setFieldEditable(false);
        setShowNoneButton(false);
    }

    private void setMarketHolidayPolicy() {
        setHolidayPolicy(new MarketHolidayPolicy());
        setVetoPolicy(new MarketHolidayVetoPolicy());
    }

    private void setQuoteBasedHolidayPolicy() {
        setHolidayPolicy(new QuoteHolidayPolicy());
        setVetoPolicy(new QuoteVetoPolicy());
    }

    public PMDate pmDate() {
        return new PMDate(getDate());
    }
}

class MarketHolidayPolicy implements HolidayPolicy {

    public boolean isHolliday(Object source, Calendar date) {
        return !new PMDate(date).isWorkingDay();
    }

    public boolean isWeekend(Object source, Calendar date) {
        return new PMDate(date).isWeekend();
    }

    public String getHollidayName(Object source, Calendar date) {
        return null;
    }

    public void addVetoPolicyListener(PolicyListener listener) {

    }

    public void removeVetoPolicyListener(PolicyListener listener) {

    }
}

class QuoteHolidayPolicy extends MarketHolidayPolicy {

    public boolean isHolliday(Object source, Calendar date) {
        return !new PMDate(date).hasQuote();
    }
}

class MarketHolidayVetoPolicy implements VetoPolicy {

    public boolean isRestricted(Object source, Calendar date) {
        PMDate pmDate = new PMDate(date);
        return pmDate.isWeekend() || !pmDate.isWorkingDay();
    }

    public boolean isRestrictNull(Object source) {
        return true;
    }

    public void addVetoPolicyListener(PolicyListener listener) {

    }

    public void removeVetoPolicyListener(PolicyListener listener) {

    }
}

class QuoteVetoPolicy extends MarketHolidayVetoPolicy {

    public boolean isRestricted(Object source, Calendar date) {
        PMDate pmDate = new PMDate(date);
        return pmDate.isWeekend() || !pmDate.hasQuote();
    }
}