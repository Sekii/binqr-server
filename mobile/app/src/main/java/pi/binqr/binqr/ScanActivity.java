package pi.binqr.binqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ScanActivity extends AppCompatActivity {
    private DecoratedBarcodeView barcodeView;
    private Set<String> scannedParts;
    private List<CheckBox> checkBoxes;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || scannedParts.contains(result.getText())) {
                // Prevent duplicate scans
                return;
            }

            byte[] rawBytes = new byte[0];
            try {
                rawBytes = result.getText().getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            scannedParts.add(result.getText());

            checkBoxes.get(0).setChecked(true);

            rawBytes = Arrays.copyOfRange(rawBytes, 290, rawBytes.length);
            saveFile(rawBytes);
        }


        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    protected void saveFile(byte[]... bytes) {
        File file = new File(Environment.getExternalStorageDirectory(), "photo.jpg");

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(file.getPath());

            fos.write(bytes[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        checkAndAskForPermissions();

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        DecoderFactory decoderFactory = new DefaultDecoderFactory(EnumSet.of(BarcodeFormat.QR_CODE), null, "ISO-8859-1", false);
        barcodeView.getBarcodeView().setDecoderFactory(decoderFactory);
        barcodeView.decodeContinuous(callback);

        scannedParts = new HashSet<>();
        checkBoxes = new ArrayList<>();

        LinearLayout progress = (LinearLayout) findViewById(R.id.progress);

        for (int i = 1; i < 6; i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(String.format(Locale.getDefault(), "%d", i));
            checkBox.setClickable(false);
            progress.addView(checkBox);
            checkBoxes.add(checkBox);
        }

        TextView first_scan_text = (TextView) findViewById(R.id.first_scan_text);
        first_scan_text.setVisibility(View.GONE);
    }

    private void checkAndAskForPermissions() {
        String[] neededPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        Set<String> ungrantedPermissions = new HashSet<>();

        for (String neededPermission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, neededPermission) != PackageManager.PERMISSION_GRANTED) {
                ungrantedPermissions.add(neededPermission);
            }
        }

        if (ungrantedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, ungrantedPermissions.toArray(new String[]{}), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
