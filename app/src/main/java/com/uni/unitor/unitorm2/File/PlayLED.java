package com.uni.unitor.unitorm2.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.uni.unitor.unitorm2.fragment.KeyLEDFragment;
import com.uni.unitor.unitorm2.layout.LayoutKey;
import com.uni.unitor.unitorm2.layout.TabHostActivity;
import com.uni.unitor.unitorm2.view.buttons.PlayButton;

import java.util.ArrayList;

/**
 * led 재생하는 스레드 (미완)**/

public  class PlayLED extends AsyncTask<String, ArrayList<String>, Boolean> {

    private String[] content;
    private View root;
    private Integer[] color;
    private Context con;
    private Boolean isPlay;

    public PlayLED(String content, View root, Context con, Integer[] color) {
        this.content = content.split("\\n+");
        this.root = root;
        this.con = con;
        this.color = color;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        StringBuilder builder = new StringBuilder();
        final ArrayList<String> list = new ArrayList<String>();
        try {
            for (String i : content) {
                String[] co = i.split("\\s+");
                if (co[0].startsWith("delay") || co[0].startsWith("d")) {
                    list.add(builder.toString());
                    builder = new StringBuilder();
                    builder.append(i + "\n");
                } else {
                    builder.append(i + "\n");
                }
            }

            for (String i:list) {
                String[] a = i.split("\\n");
                String delay = "0";
                if (a[0].startsWith("d")||a[0].startsWith("delay")) {
                    try {
                        delay = a[0].split("\\s+")[1];
                    } catch (Exception e) {

                    }
                }

                ArrayList<String>  on= new ArrayList<String>();
                ArrayList<String> off = new ArrayList<String>();

                for (String k:a) {
                    if (k.startsWith("d")||k.startsWith("delay")) {
                        continue;
                    } else {
                        if (k.startsWith("o")||k.startsWith("on")) {
                            String[] in = k.split("\\s+");
                            try {
                                on.add(in[1] + " " + in[2]+ "\n" +color[Integer.parseInt(in[4])]);
                            } catch (Exception e) {

                            }
                        } else if (k.startsWith("f")||k.startsWith("off")) {
                            String[] in = k.split("\\s+");
                            try {
                                off.add(in[1] + " " + in[2]);
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                publishProgress(on, off);
                if (!delay.equals("0")) {
                    synchronized (this) {
                        wait(Integer.parseInt(delay));
                    }
                }
            }

//            for (String i:list) {
//                String[] a = i.split("\\n");
//                if (a[0].startsWith("d")||a[0].startsWith("delay")) {
//                    publishProgress("start", a[0].split("\\s+")[1], i);
//                } else {
//                    publishProgress("start", "0", i);
//                }
//                isPlay = true;
//                while (isPlay) {
//                    synchronized (this) {
//                        wait(1);
//                    }
//                }
//            }

//            for (String i : content) {
//                String[] in = i.split("\\s+");
//                if (in[0].equals("d")||in[0].equals("delay")) {
//                    synchronized (this) {
//                        this.wait(Long.parseLong(in[1]));
//                    }
//                } else {
//                    publishProgress(i);
//                }
//            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(final ArrayList<String>... values) {
        Intent intent = new Intent();
        intent.setAction(LayoutKey.BROAD_ACTION_ON);
        intent.putStringArrayListExtra("on",values[0]);
        con.sendBroadcast(intent);
        Intent intent1 = new Intent();
        intent1.setAction(LayoutKey.BROAD_ACTION_OFF);
        intent1.putStringArrayListExtra("off",values[1]);
        con.sendBroadcast(intent1);
//        if (values[0].equals("start")) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        for (String i:values[2].split("\\n")) {
//                            if (i.startsWith("d")||i.startsWith("delay")) {
//                                continue;
//                            } else {
//                                final String[] in = i.split("\\s+");
//                                if (i.startsWith("o")||i.startsWith("on")) {
//                                    if (in[3].equals("a")||in[3].equals("auto")) {
//                                        ((TabHostActivity) con).runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).onLED(color[Integer.parseInt(in[4])]);
//                                            }
//                                        });
//                                        //publishProgress("o", in[1] + " " + in[2], in[4]);
//                                    } else {
//                                        ((TabHostActivity) con).runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).onLED(Color.parseColor("#" + in[3]));
//                                            }
//                                        });
//                                        //publishProgress("o", in[1] + " " + in[2], "#" + in[3]);
//                                    }
//                                } else if (i.startsWith("f")||i.startsWith("off")) {
//                                    ((TabHostActivity) con).runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).offLED();
//                                        }
//                                    });
//                                    //publishProgress("f", in[1] + " " + in[2]);
//                                }
//                            }
//                        }
//                        isPlay = false;
//                    } catch (Exception e) {
//
//                    }
//                }
//            }, Integer.parseInt(values[1]));
//        } else if (values[0].equals("o")){
//            ((PlayButton)root.findViewWithTag(values[1])).onLED(color[Integer.parseInt(values[2])]);
//        } else if (values[0].equals("f")) {
//            ((PlayButton)root.findViewWithTag(values[1])).offLED();
//        }


//        try {
//            String[] in = values[0].split("\\s+");
//            if (in[0].equals("o")||in[0].equals("on")) {
//                if (in[3].equals("a")||in[3].equals("auto")) {
//                    ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).onLED(color[Integer.parseInt(in[4])]);
//                } else {
//                    ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).onLEDHtml(Color.parseColor("#" + in[3]));
//                }
//            } else if (in[0].equals("f")||in[0].equals("off")) {
//                ((PlayButton)root.findViewWithTag(in[1] + " " + in[2])).offLED();
//            }
//        } catch (Exception e) {
//
//        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Toast.makeText(con, "LEDF", Toast.LENGTH_SHORT).show();
    }
}
