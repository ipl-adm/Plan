/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.main.java.com.djrapitops.plan.utilities;

import java.util.Date;
import main.java.com.djrapitops.plan.utilities.FormatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import static org.junit.Assert.*;
import org.junit.Test;
import test.java.utils.*;

/**
 *
 * @author Rsl1122
 */
public class FormatUtilsTest {

    /**
     *
     */
    public FormatUtilsTest() {
    }

    /**
     *
     */
    @Test
    public void testFormatTimeAmount() {
        String string = "" + 1000L;
        String expResult = "1s";
        String result = FormatUtils.formatTimeAmount(string);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testFormatTimeAmountSinceDate() {
        Date before = new Date(300000L);
        Date now = new Date(310000L);
        String expResult = "10s";
        String result = FormatUtils.formatTimeAmountSinceDate(before, now);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testFormatTimeStamp() {
        String string = "0";
        String expResult = "Jan 01 02:00:00";
        String result = FormatUtils.formatTimeStamp(string);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testFormatTimeAmountSinceString() {
        String string = "" + new Date(310000L).toInstant().getEpochSecond() * 1000L;
        Date now = new Date(300000L);
        String expResult = "10s";
        String result = FormatUtils.formatTimeAmountSinceString(string, now);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testRemoveLetters() {
        String dataPoint = "435729847jirggu.eiwb¤#¤%¤#";
        String expResult = "435729847.";
        String result = FormatUtils.removeLetters(dataPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testRemoveNumbers() {
        String dataPoint = "34532453.5 $";
        String expResult = "$";
        String result = FormatUtils.removeNumbers(dataPoint);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testRemoveNumbers2() {
        String dataPoint = "l43r4545tl43  4.5";
        String expResult = "lrtl";
        String result = FormatUtils.removeNumbers(dataPoint);
        assertEquals(expResult, result);
    }
    
    /**
     *
     */
    @Test
    public void testParseVersionNumber() {
        String versionString = "2.10.2";
        int expResult = 21002;
        int result = FormatUtils.parseVersionNumber(versionString);
        assertEquals(expResult, result);
    }
    
    /**
     *
     */
    @Test
    public void testVersionNumber() {
        String versionString = "2.10.2";
        String versionString2 = "2.9.3";
        int result = FormatUtils.parseVersionNumber(versionString);
        int result2 = FormatUtils.parseVersionNumber(versionString2);
        assertTrue("Higher version not higher", result > result2);
    }

    /**
     *
     */
    @Test
    public void testMergeArrays() {
        String[][] arrays = new String[][]{new String[]{"Test", "One"}, new String[]{"Test", "Two"}};
        String[] expResult = new String[]{"Test", "One", "Test","Two"};
        String[] result = FormatUtils.mergeArrays(arrays);
        assertArrayEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testFormatLocation() {
        World mockWorld = MockUtils.mockWorld();
        Location loc = new Location(mockWorld, 0, 0, 0);
        String expResult = "x 0 z 0 in World";
        String result = FormatUtils.formatLocation(loc);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testCutDecimals() {
        double d = 0.05234;
        String expResult = "0,05";
        String result = FormatUtils.cutDecimals(d);
        assertEquals(expResult, result);
    }

    /**
     *
     */
    @Test
    public void testCutDecimals2() {
        double d = 0.05634;
        String expResult = "0,06";
        String result = FormatUtils.cutDecimals(d);
        assertEquals(expResult, result);
    }

}
