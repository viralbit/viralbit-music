package tech.viralbit.music;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.annotation.PluginMethod;

@CapacitorPlugin(
  name = "MediaStoreReader",
  permissions = {
    @Permission(strings = { Manifest.permission.READ_MEDIA_AUDIO }, alias = "audio33"),
    @Permission(strings = { Manifest.permission.READ_EXTERNAL_STORAGE }, alias = "audioLegacy")
  }
)
public class MediaStorePlugin extends Plugin {

  @PluginMethod
  public void requestAudioPermission(PluginCall call) {
    if (Build.VERSION.SDK_INT >= 33) requestPermissionForAlias("audio33", call, "permCallback");
    else requestPermissionForAlias("audioLegacy", call, "permCallback");
  }

  @PermissionCallback
  private void permCallback(PluginCall call) {
    call.resolve();
  }

  @PluginMethod
  public void listAudio(PluginCall call) {
    JSArray songs = new JSArray();

    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    String[] projection = new String[] {
      MediaStore.Audio.Media._ID,
      MediaStore.Audio.Media.TITLE,
      MediaStore.Audio.Media.ARTIST
    };

    String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

    Cursor cursor = getContext().getContentResolver().query(
      uri, projection, selection, null,
      MediaStore.Audio.Media.TITLE + " ASC"
    );

    if (cursor != null) {
      int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

      while (cursor.moveToNext()) {
        long id = cursor.getLong(idCol);
        String title = cursor.getString(titleCol);
        String artist = cursor.getString(artistCol);

        Uri contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

        JSObject o = new JSObject();
        o.put("id", String.valueOf(id));
        o.put("title", title);
        o.put("artist", artist);
        o.put("uri", contentUri.toString());
        songs.put(o);
      }
      cursor.close();
    }

    JSObject ret = new JSObject();
    ret.put("songs", songs);
    call.resolve(ret);
  }
}
