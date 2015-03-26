package de.dihco.android.stechuhr.Backup;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;

public class BackupGoogleDriveActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final int REQUEST_CODE_RESOLUTION = 1;
    protected static final int REQUEST_CODE_CREATOR = 2;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Button btnCreate = (Button) findViewById(R.id.btnCreateBackup);
        btnCreate.setEnabled(false);
    }


    //region Verbindung herstellen / halten / trennen
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    ComLib.ShowMessage(getString(R.string.GoogleDriveConnectError));
                    finish();
                }

                break;
            case REQUEST_CODE_CREATOR:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    ComLib.ShowMessage("File created with ID: " + driveId);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Button btnCreate = (Button) findViewById(R.id.btnCreateBackup);
        btnCreate.setEnabled(true);
//        ComLib.ShowMessage( "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Button btnCreate = (Button) findViewById(R.id.btnCreateBackup);
        btnCreate.setEnabled(false);
//        ComLib.ShowMessage( "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
//        ComLib.ShowMessage( "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
//            ComLib.ShowMessage("Error");
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            ComLib.ShowMessage(getString(R.string.GoogleDriveConnectError) + "\n" + e.getMessage());
        }
    }
//endregion


    public void btnCreateBackupClick(View view) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setTitle(StrHelp.getBackupFileName())
                            .setMimeType("text/plain").build();

                    final DriveContents driveContents = result.getDriveContents();
                    //write content to DriveContents
                    OutputStream outputStream = driveContents.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    try {
                        ComLib.createBackup(writer);
                    } catch (IOException e) {
                        ComLib.ShowMessage(e.getMessage());
                    }


                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContents)
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        ComLib.ShowMessage("Unable to send intent\n" + e.getMessage());
                    }
                }
            };

    //region Backup erstellen
//    public void btnCreateBackupClick(View view) {
//        if (mGoogleApiClient != null) {
//            if (mGoogleApiClient.isConnected()) {
//                Drive.DriveApi.newDriveContents(mGoogleApiClient)
//                        .setResultCallback(driveContentsCallback);
//            } else {
//                ComLib.ShowMessage("Google Drive Account ist nicht verbunden.");
//            }
//        } else {
//            ComLib.ShowMessage("Google Drive Api ist nicht vorhanden.");
//        }
//    }
//
//    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
//            ResultCallback<DriveApi.DriveContentsResult>() {
//                @Override
//                public void onResult(DriveApi.DriveContentsResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        ComLib.ShowMessage("Ein Fehler ist bei der Erstellung der Datei aufgetreten.");
//                        return;
//                    }
//                    final DriveContents driveContents = result.getDriveContents();
//
//                    // Perform I/O off the UI thread.
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            // write content to DriveContents
//                            OutputStream outputStream = driveContents.getOutputStream();
//                            Writer writer = new OutputStreamWriter(outputStream);
//                            try {
//                                ComLib.createBackup(writer);
//                            } catch (IOException e) {
//                                ComLib.ShowMessage(e.getMessage());
//                            }
//
//                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                    .setTitle(StrHelp.getBackupFileName())
//                                    .setMimeType("text/plain")
//                                    .setStarred(true).build();
//
//                            // create a file on root folder
//                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
//                                    .createFile(mGoogleApiClient, changeSet, driveContents)
//                                    .setResultCallback(fileCallback);
//                        }
//                    }.start();
//                }
//            };
//
//    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
//            ResultCallback<DriveFolder.DriveFileResult>() {
//                @Override
//                public void onResult(DriveFolder.DriveFileResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        ComLib.ShowMessage("Error while trying to create the file");
//                        return;
//                    }
//                    ComLib.ShowMessage("Created a file with content: " + result.getDriveFile().getDriveId());
//                }
//            };
    //endregion

}
