package com.greenledge.quran;

import android.os.Bundle;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class MyPdfViewerActivity extends PdfViewerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public int getPreviousPageImageResource() {
        return R.drawable.left_arrow;
    }

    public int getNextPageImageResource() {
        return R.drawable.right_arrow;
    }

    public int getZoomInImageResource() {
        return R.drawable.zoom_in;
    }

    public int getZoomOutImageResource() {
        return R.drawable.zoom_out;
    }

    public int getPdfPasswordLayoutResource() {
        return R.layout.pdf_file_password;
    }

    public int getPdfPageNumberResource() {
        return R.layout.dialog_pagenumber;
    }

    public int getPdfPasswordEditField() {
        return R.id.etPassword;
    }

    public int getPdfPasswordOkButton() {
        return R.id.btOK;
    }

    public int getPdfPasswordExitButton() {
        return R.id.btExit;
    }

    public int getPdfPageNumberEditField() {
        return R.id.pagenum_edit;
    }

	// @Override
	// public int getNextPageImageResource() {
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPageNumberEditField() {
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPageNumberResource() {
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPasswordEditField() {
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPasswordExitButton() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPasswordLayoutResource() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int getPdfPasswordOkButton() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

}
