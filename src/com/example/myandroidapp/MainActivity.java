package com.example.myandroidapp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.example.myandroidapp.R;

import pixy.image.tiff.FieldType;
import pixy.image.tiff.TiffTag;
import pixy.meta.Metadata;
import pixy.meta.MetadataType;
import pixy.meta.Thumbnail;
import pixy.meta.adobe.IPTC_NAA;
import pixy.meta.adobe.IRB;
import pixy.meta.adobe.ImageResourceID;
import pixy.meta.adobe.JPEGQuality;
import pixy.meta.adobe.ThumbnailResource;
import pixy.meta.adobe.VersionInfo;
import pixy.meta.adobe._8BIM;
import pixy.meta.exif.Exif;
import pixy.meta.exif.ExifTag;
import pixy.meta.exif.ExifThumbnail;
import pixy.meta.exif.JpegExif;
import pixy.meta.exif.TiffExif;
import pixy.meta.iptc.IPTCApplicationTag;
import pixy.meta.iptc.IPTCDataSet;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		ImageView image = (ImageView) findViewById(R.id.test_image);
		InputStream fin = getResources().openRawResource(
	            getResources().getIdentifier("nikon",
	            "raw", getPackageName()));
		InputStream fin2 = getResources().openRawResource(
	            getResources().getIdentifier("table",
	            "raw", getPackageName()));
		ByteArrayOutputStream fout = new ByteArrayOutputStream();
		Map<MetadataType, Metadata> metadataMap = null;
		try {
			_8BIM thumbnail = null;
			metadataMap = Metadata.readMetadata(fin);
			IRB irb = (IRB)metadataMap.get(MetadataType.PHOTOSHOP_IRB);
			if(irb != null)
				thumbnail = irb.getThumbnailResource();
			if(thumbnail == null) {
				Exif exif = (Exif)metadataMap.get(MetadataType.EXIF);
				ExifThumbnail exifThumbnail = exif.getThumbnail();
				if(exifThumbnail != null) { // Convert ExifThumbnail to ThumbnailResource and insert into image
					if(exifThumbnail.getRawImage() != null)
						thumbnail = new ThumbnailResource(exifThumbnail.getRawImage());
					else if(exifThumbnail.getDataType() == Thumbnail.DATA_TYPE_KJpegRGB)
						thumbnail = new ThumbnailResource(ImageResourceID.THUMBNAIL_RESOURCE_PS5, exifThumbnail.getDataType(), exifThumbnail.getWidth(), exifThumbnail.getHeight(), exifThumbnail.getCompressedImage());
				}
			}
			// Insert JPEG quality 8BIM
			_8BIM jpegQuality = new JPEGQuality(JPEGQuality.Quality.QUALITY_10_MAXIMUM, 
					JPEGQuality.Format.FORMAT_STANDARD, JPEGQuality.ProgressiveScans.PROGRESSIVE_5_SCANS);		
			// Insert JPEG version info 8BIM
			_8BIM versionInfo = new VersionInfo(1, true, "Writer", "Reader", 1);
			// Insert IPTC_NAA 8BIM
			IPTC_NAA iptc = new IPTC_NAA();
			iptc.addDataSet(new IPTCDataSet(IPTCApplicationTag.COPYRIGHT_NOTICE, "Copyright 2014-2015, yuwen_66@yahoo.com"));
			iptc.addDataSet(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, "Welcome 'icafe' user!"));
			iptc.addDataSet(new IPTCDataSet(IPTCApplicationTag.CATEGORY, "ICAFE"));
			
			Metadata.insertIRB(fin2, fout, Arrays.asList(jpegQuality, thumbnail, versionInfo, iptc), true);
			fin.close();
			fin2.close();
			fout.close();
		} catch (IOException e) {
			image.setImageResource(R.drawable.ic_launcher);
		}
		
		FileOutputStream outputStream;

		try {
			outputStream = openFileOutput("thumbnail-inserted.jpg", MODE_WORLD_READABLE);
			outputStream.write(fout.toByteArray());
			outputStream.close();
		} catch (Exception e) {
			Bitmap bMap = BitmapFactory.decodeByteArray(fout.toByteArray(), 0, fout.toByteArray().length);
			image.setImageBitmap(bMap);
		}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}		
		return super.onOptionsItemSelected(item);
	}
	
	// This method is for testing only
	private static Exif populateExif(Class<?> exifClass) throws IOException {
		// Create an EXIF wrapper
		Exif exif = exifClass == (TiffExif.class)?new TiffExif() : new JpegExif();
		exif.addImageField(TiffTag.WINDOWS_XP_AUTHOR, FieldType.WINDOWSXP, "Author");
		exif.addImageField(TiffTag.WINDOWS_XP_KEYWORDS, FieldType.WINDOWSXP, "Copyright;Author");
		DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		exif.addExifField(ExifTag.EXPOSURE_TIME, FieldType.RATIONAL, new int[] {10, 600});
		exif.addExifField(ExifTag.FNUMBER, FieldType.RATIONAL, new int[] {49, 10});
		exif.addExifField(ExifTag.ISO_SPEED_RATINGS, FieldType.SHORT, new short[]{273});
		//All four bytes should be interpreted as ASCII values - represents [0220] - new byte[]{48, 50, 50, 48}
		exif.addExifField(ExifTag.EXIF_VERSION, FieldType.UNDEFINED, "0220".getBytes());
		exif.addExifField(ExifTag.DATE_TIME_ORIGINAL, FieldType.ASCII, formatter.format(new Date()));
		exif.addExifField(ExifTag.DATE_TIME_DIGITIZED, FieldType.ASCII, formatter.format(new Date()));
		exif.addExifField(ExifTag.FOCAL_LENGTH, FieldType.RATIONAL, new int[] {240, 10});		
		// Insert ThumbNailIFD
		// Since we don't provide thumbnail image, it will be created later from the input stream
		exif.setThumbnailRequired(true);
		
		return exif;
	}
}