package com.hungle.tools.moneyutils.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

public class SymbolMapper {
    private static final Logger LOGGER = Logger.getLogger(SymbolMapper.class);

    private final Map<String, List<SymbolMapperEntry>> mapByMsMoneySymbol = new HashMap<String, List<SymbolMapperEntry>>();
    private final Map<String, List<SymbolMapperEntry>> mapByQuotesSourceSymbol = new HashMap<String, List<SymbolMapperEntry>>();
    private final List<SymbolMapperEntry> entries = new ArrayList<SymbolMapperEntry>();

    public void load(File file) throws IOException {
        CsvReader csvReader = null;
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            csvReader = new CsvReader(reader);
            csvReader.readHeaders();
            load(csvReader);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }

    }

    public void load(CsvReader csvReader) throws IOException {
        SymbolMapperEntry entry;
        while (csvReader.readRecord()) {
            entry = new SymbolMapperEntry();
            if (LOGGER.isDebugEnabled()) {
                String line = csvReader.getRawRecord();
                LOGGER.debug(line);
            }

            entry.load(csvReader);

            updateMapBys(entry);

            entries.add(entry);
        }
    }

    private void updateMapBys(SymbolMapperEntry entry) {
        String msMoneySymbol = entry.getMsMoneySymbol();
        String quotesSourceSymbol = entry.getQuotesSourceSymbol();

        List<SymbolMapperEntry> list = null;
        list = mapByMsMoneySymbol.get(msMoneySymbol);
        if (list == null) {
            list = new ArrayList<SymbolMapperEntry>();
            mapByMsMoneySymbol.put(msMoneySymbol, list);
        }
        list.add(entry);

        list = mapByQuotesSourceSymbol.get(quotesSourceSymbol);
        if (list == null) {
            list = new ArrayList<SymbolMapperEntry>();
            mapByQuotesSourceSymbol.put(quotesSourceSymbol, list);
        }
        list.add(entry);
    }

    public List<SymbolMapperEntry> entriesByQuoteSource(String quoteSourceSymbol) {
        List<SymbolMapperEntry> list = mapByQuotesSourceSymbol.get(quoteSourceSymbol);
        if (list == null) {
            return null;
        }
        return list;
    }

    public boolean hasEntry(String ticker) {
        if (mapByQuotesSourceSymbol.get(ticker) != null) {
            return true;
        }
        if (mapByMsMoneySymbol.get(ticker) != null) {
            return true;
        }

        return false;
    }

    public boolean getIsMutualFund(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isMutualFund()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isMutualFund()) {
                    return true;
                }
            }
        }
        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }

    public boolean getIsOptions(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isOptions()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isOptions()) {
                    return true;
                }
            }
        }

        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }

    public boolean getIsBond(String ticker) {
        List<SymbolMapperEntry> list = null;

        list = mapByQuotesSourceSymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return true;
                }
            }
        }

        list = mapByMsMoneySymbol.get(ticker);
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                if (entry.isBond()) {
                    return true;
                }
            }
        }

        // log.warn("Cannot find SymbolMapperEntry for tickerName=" + ticker);
        return false;
    }

    public List<SymbolMapperEntry> getEntries() {
        return entries;
    }

    public static SymbolMapper createDefaultSymbolMapper() {
        SymbolMapper symbolMapper = new SymbolMapper();
        String fileName = "mapper.csv";
        File symbolMapperFile = new File(fileName);
        LOGGER.info("Looking for mapper=" + symbolMapperFile.getAbsoluteFile().getAbsolutePath());
        if (symbolMapperFile.exists()) {
            try {
                symbolMapper.load(symbolMapperFile);
                LOGGER.info("Loaded symbolMapperFile=" + symbolMapperFile);
            } catch (IOException e) {
                LOGGER.warn("Cannot load symbolMapperFile=" + symbolMapperFile);
            }
        } else {
            LOGGER.info("No " + fileName + " file.");
        }
        return symbolMapper;
    }

}
