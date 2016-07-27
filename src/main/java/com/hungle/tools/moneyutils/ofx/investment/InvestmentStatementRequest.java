package com.hungle.tools.moneyutils.ofx.investment;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.XmlBeansUtils;

import net.ofx.types.x2003.x04.FinancialInstitution;
import net.ofx.types.x2003.x04.IncPosition;
import net.ofx.types.x2003.x04.IncTransaction;
import net.ofx.types.x2003.x04.InvestmentAccount;
import net.ofx.types.x2003.x04.InvestmentStatementRequestMessageSetV1;
import net.ofx.types.x2003.x04.InvestmentStatementTransactionRequest;
import net.ofx.types.x2003.x04.LanguageEnum;
import net.ofx.types.x2003.x04.OFX;
import net.ofx.types.x2003.x04.OFXDocument;
import net.ofx.types.x2003.x04.SignonRequest;
import net.ofx.types.x2003.x04.SignonRequestMessageSetV1;

public class InvestmentStatementRequest {
    private static final Logger log = Logger.getLogger(InvestmentStatementRequest.class);

    private XmlOptions xmlOptions;

    private OFXDocument ofxDocument;

    private String userId;

    private String userPassword;

    private String org;

    public InvestmentStatementRequest(String userId, String userPassword, String org) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.org = org;
        this.ofxDocument = createRequestOfxDocument();
    }

    private OFXDocument createRequestOfxDocument() {
        this.xmlOptions = XmlBeansUtils.createXmlOptions();
        OFXDocument ofxDocument = OFXDocument.Factory.newInstance(xmlOptions);
        OFX ofx = ofxDocument.addNewOFX();

        XmlBeansUtils.insertProcInst(ofx);

        addSignonRequestMessageSetV1(ofx);

        addTodo(ofx);

        OfxUtils.localizeXmlFragment(ofxDocument);

        return ofxDocument;
    }

    private InvestmentStatementRequestMessageSetV1 addTodo(OFX ofx) {
        InvestmentStatementRequestMessageSetV1 root = ofx.addNewINVSTMTMSGSRQV1();

        InvestmentStatementTransactionRequest node1 = root.addNewINVSTMTTRNRQ();
        String trnuid = "" + XmlBeansUtils.getRandom().nextLong();
        node1.setTRNUID(trnuid);

        net.ofx.types.x2003.x04.InvestmentStatementRequest request = node1.addNewINVSTMTRQ();
        InvestmentAccount node2 = request.addNewINVACCTFROM();
        node2.setBROKERID("vanguard.com");
        node2.setACCTID("88037142018");

        IncTransaction node3 = request.addNewINCTRAN();
        node3.setDTSTART("20101124");
        node3.setINCLUDE(net.ofx.types.x2003.x04.BooleanType.Y);

        request.setINCOO(net.ofx.types.x2003.x04.BooleanType.Y);

        IncPosition node4 = request.addNewINCPOS();
        node4.setINCLUDE(net.ofx.types.x2003.x04.BooleanType.Y);

        request.setINCBAL(net.ofx.types.x2003.x04.BooleanType.Y);

        return root;
    }

    private SignonRequestMessageSetV1 addSignonRequestMessageSetV1(OFX ofx) {
        SignonRequestMessageSetV1 root = ofx.addNewSIGNONMSGSRQV1();
        // 2.5.1.2 Signon Request <SONRQ>
        SignonRequest sonRq = root.addNewSONRQ();

        String dtclient = XmlBeansUtils.getDtClient();
        // Date and time of the request from the client computer, datetime
        sonRq.setDTCLIENT(dtclient);

        // User identification string.
        sonRq.setUSERID(this.userId);

        // User password on server
        sonRq.setUSERPASS(this.userPassword);

        // Requested language for text responses, language
        sonRq.setLANGUAGE(LanguageEnum.ENG);

        // Financial-Institution-identification aggregate
        FinancialInstitution fi = sonRq.addNewFI();
        fi.setORG(this.org);

        sonRq.setAPPID("le.com");
        sonRq.setAPPVER("0100");

        return root;
    }

    public void save(File file) throws IOException {
        ofxDocument.save(file, xmlOptions);
    }

    public OFXDocument getOfxDocument() {
        return ofxDocument;
    }

    public void setOfxDocument(OFXDocument ofxDocument) {
        this.ofxDocument = ofxDocument;
    }
}
