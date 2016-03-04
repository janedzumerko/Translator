package com.example.jane.timskaapplication101.files;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jane.timskaapplication101.BaseFragment;
import com.example.jane.timskaapplication101.MainActivity;
import com.example.jane.timskaapplication101.MainNavFragment;
import com.example.jane.timskaapplication101.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.jane.timskaapplication101.files.SecondFrag;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Created by Jane on 7/27/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>

{


    
    public static List<PDFItem> pdfItems;
    Context context;
    static long startTime =0;
    public static BaseFont bfComic;
    public static Font font;

    RVAdapter(Context context, List<PDFItem> pdfItems)  {
        this.context = context;
        this.pdfItems = pdfItems;


        try {
            bfComic = BaseFont.createFont
                    (context.getAssets() + "/fonts/COUR.ttf" , "Cp1251", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        font = new com.itextpdf.text.Font(bfComic, 12);
    }



    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CardView cv;
        private TextView pdfName;
        private TextView pdfSize;
        private ImageView pdfPhoto;
        private Context context;
        private Button open;
        private Button translate;


        PersonViewHolder(Context context, View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            pdfName = (TextView)itemView.findViewById(R.id.pdf_name);
            pdfSize = (TextView)itemView.findViewById(R.id.pdf_size);
            pdfPhoto = (ImageView)itemView.findViewById(R.id.pdf_photo);
            this.context = context;
            open = (Button) itemView.findViewById(R.id.openfile);
            translate = (Button) itemView.findViewById(R.id.translatefile);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(v.getContext(), "Long click", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Short click", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_layout,
                viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(viewGroup.getContext(), v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {
        final int j = i;
        personViewHolder.pdfName.setText(pdfItems.get(j).name);
        personViewHolder.pdfSize.setText(pdfItems.get(j).size);
        personViewHolder.pdfPhoto.setImageResource(pdfItems.get(j).photoID);

        personViewHolder.translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String tmp = pdfItems.get(j).getPath() + "/" +
                            pdfItems.get(j).getName();
                    Log.i("GIOOA dva","fajl path=" + tmp);

                    String tmp2 = tmp.substring(0,tmp.length()-4);
                    Log.i("GIOOA dva", "fajl path=" + tmp2);
                    createFileFull(tmp,
                            tmp2 + "_translate.pdf",
                            pdfItems.get(j).getPath(),
                            pdfItems.get(j).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        personViewHolder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(pdfItems.get(j).getPath() + "/" + pdfItems.get(j).getName());
                PackageManager packageManager = v.getContext().getPackageManager();
                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                testIntent.setType("application/pdf");
                List list = packageManager.queryIntentActivities(testIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                if (list.size() > 0 && file.isFile()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, "application/pdf");
                    v.getContext().startActivity(intent);
                }

            }

        });
    }



    @Override
    public int getItemCount() {
        return pdfItems.size();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static double getTimeSec(long t){
        double time = (t - startTime)/1000;
        return time;
    }

    public static String getProgress(int now, int total){
        double d = (double)now/total*100.0;
        return d + " %";
    }





    public static void createFileFull(String pdfIN, String pdfOUT,
                                      String parent,
                                      String name) throws
            Exception   {
        String argument = pdfIN + "#" + pdfOUT + "#" + parent + "#" + name ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new FileTranslate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    argument);
        } else {
            new FileTranslate().execute(argument);
        }


    }




    // Async Task Class
    static class FileTranslate extends AsyncTask<String, String, String> {

        public String langaugeUserChoose;
        private String outputFile;


        @Override
        protected void onPreExecute() {
            BaseFragment.showLoading();
        }


        @Override
        protected String doInBackground(String... f_url) {
            String[] args = new String[3];
            try {



                langaugeUserChoose = SecondFrag.getLangaugeForFileTranslating();

                args = f_url[0].split("#");
                Log.i("GIOOA dva", "Method start" + getTimeSec(System.currentTimeMillis())
                        + " sec.");


                PdfReader reader = new PdfReader(args[0]);

                int totalPages = reader.getNumberOfPages();
                Log.i("GIOOA dva", "total pages: " + totalPages);

                outputFile = args[1].substring(0,args[1].length()-4);
                outputFile = outputFile+ "_" + langaugeUserChoose + ".pdf";
                Log.i("GIOOA dva", "Output file: " + outputFile);

                Document pdfFile = new Document(PageSize.A4, 36, 72, 108, 180);
                PdfWriter.getInstance(pdfFile, new FileOutputStream(outputFile));
                pdfFile.open();


                Paragraph p = new Paragraph("", font );

                for (int i = 1; i <= totalPages; i++) {

                    Log.i("GIOOA dva", getProgress(i, totalPages));

                    String line = PdfTextExtractor.getTextFromPage(reader, i);
                    Log.i("GIOOA dva","Line: " + line);

                    String translatedString = Translate.execute(line,
                            Language.AUTO_DETECT,
                            Language.fromString(langaugeUserChoose));


                    p.add(translatedString);
                    p.add("\n");
                    Log.i("GIOOA dva", "Krajo");

                }


                pdfFile.add(p);
                pdfFile.close();

                Log.i("GIOOA dva","Method finished: " + getTimeSec(System.currentTimeMillis()) + " sec.");

                Log.i("GIOOA dva", "TRanslate method");



            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return args[2] + "#" + args[3];
        }


        protected void onProgressUpdate(String... progress) {
            // Set progress percentage

        }

        @Override
        protected void onPostExecute(String file_url) {
            Log.i("GIOOA dva", "Kraj na async");
            Log.i("GIOOA dva", file_url);



            String [] zaTmp = file_url.split("#");
            // 0 - parent   1 - name


            String TranslatedFileFinalName = zaTmp[1].substring(0,zaTmp[1].length()-4);
            TranslatedFileFinalName += "_translate" + "_" + langaugeUserChoose + ".pdf";
            File fajltmp = new File(zaTmp[0]+"/"+TranslatedFileFinalName);

            PDFItem tmp = new PDFItem(TranslatedFileFinalName,
                    MainActivity.getFileSize(fajltmp.length()),
                    zaTmp[0],
                    R.drawable.logo_after_translate);
            MainActivity.getPdfs().add(tmp);
            SecondFrag.adapter.notifyDataSetChanged();
            //notify adapter

            BaseFragment.dismissLoading();
        }
    }


}