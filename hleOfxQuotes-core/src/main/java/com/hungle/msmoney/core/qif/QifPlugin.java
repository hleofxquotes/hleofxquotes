package com.hungle.msmoney.core.qif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.Locale;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.Price;

public class QifPlugin {
    private static final Logger LOGGER = Logger.getLogger(QifPlugin.class);

    private Invocable invocable;

    public QifPlugin(Invocable invocable) {
        this.invocable = invocable;
    }

    public String toQifPriceString(Double price) {
        String str = null;

        try {
            Object result = invocable.invokeFunction("toQifPriceString", price);
            if (result != null) {
                str = result.toString();
            }
        } catch (NoSuchMethodException e) {
            LOGGER.error(e, e);
        } catch (ScriptException e) {
            LOGGER.error(e, e);
        }

        return str;
    }

    static QifPlugin createQifPlugin() {
        QifPlugin qifPlugin = null;
    
        String userHome = System.getProperty("user.home", ".");
        File homeDir = new File(userHome);
        File topDir = new File(homeDir, ".hleofxquotes/qif");
        if (!topDir.exists()) {
            topDir.mkdirs();
        }
        File jsFile = new File(topDir, "qif.js");
        if (! jsFile.exists()) {
            return null;
        }
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            LOGGER.info("Loading javascript file=" + jsFile);
            engine.eval(new FileReader(jsFile));
            Invocable invocable = (Invocable) engine;
            qifPlugin = new QifPlugin(invocable);
        } catch (FileNotFoundException e) {
            LOGGER.warn(e);
        } catch (ScriptException e) {
            LOGGER.warn(e);
        }
        
        return qifPlugin;
    }

    public static final NumberFormat createPriceFormatter(String language, String country) {
        Locale locale = new Locale(language, country);
        final NumberFormat priceFormatter = Price.createPriceFormatter(locale);
        return priceFormatter;
    }
    
    public static final String formatPrice(String language, String country, Double price) {
        final NumberFormat priceFormatter = createPriceFormatter(language, country);
        final String formattedString = priceFormatter.format(price);
        return formattedString;
    }

}
