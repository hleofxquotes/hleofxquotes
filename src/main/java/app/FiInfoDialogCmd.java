package app;

import java.awt.Dialog;

import javax.swing.WindowConstants;

import com.le.tools.moneyutils.beansbinding.FiInfoDialog;
import com.le.tools.moneyutils.fi.VelocityUtils;
import com.le.tools.moneyutils.fi.model.bean.FiInfoBean;

public class FiInfoDialogCmd {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        VelocityUtils.initVelocity();

        try {
            FiInfoBean fiInfoBean = new FiInfoBean();
            fiInfoBean.setName("American Express");
            fiInfoBean.setId("3101");
            fiInfoBean.setOrg("AMEX");
            fiInfoBean.setUrl("https://online.americanexpress.com/myca/ofxdl/desktop/desktopDownload.do?request_type=nl_ofxdownload");
            fiInfoBean.setBrokerId("");
            fiInfoBean.setLogin("hlehle");
            fiInfoBean.setPassword("141514s");
            FiInfoDialog dialog = new FiInfoDialog(fiInfoBean);
            dialog.setTitle("Financial Institution");
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setVisible(true);
            FiInfoBean resultBean = dialog.getFiInfoBean();
            if (resultBean != null) {
                fiInfoBean = resultBean;
            } else {
                // cancel
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
