package br.com.tectoy.tectoysunmi.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class BitmapActivity extends BaseActivity {
    ImageView mImageView;
    TextView mTextView1, mTextView6;
    LinearLayout ll, ll1, ll2;
    Bitmap bitmap, bitmap1;
    CheckBox mCheckBox1, mCheckBox2;

    int mytype;
    int myorientation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        setMyTitle(R.string.bitmap_title);
        setBack();
        initView();

        ll.setVisibility(View.GONE);


    }

    private void initView() {
        mTextView1 = findViewById(R.id.pic_align_info);

        mCheckBox1 = findViewById(R.id.pic_width);
        mCheckBox2 = findViewById(R.id.pic_height);
        ll = findViewById(R.id.pic_style);
        mTextView6 = findViewById(R.id.cut_paper_info);

        findViewById(R.id.pic_align).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] pos = new String[]{getResources().getString(R.string.align_left), getResources().getString(R.string.align_mid), getResources().getString(R.string.align_right)};
                final ListDialog listDialog = DialogCreater.createListDialog(BitmapActivity.this, getResources().getString(R.string.align_form), getResources().getString(R.string.cancel), pos);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView1.setText(pos[position]);
                        TectoySunmiPrint.getInstance().setAlign(position);

                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });


        findViewById(R.id.cut_paper_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] cut = new String[]{"Sim","Não"};
                final ListDialog listDialog = DialogCreater.createListDialog(BitmapActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), cut);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView6.setText(cut[position]);

                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });
        mImageView = findViewById(R.id.bitmap_imageview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test, options);

        }

        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test1, options);
            bitmap1 = scaleImage(bitmap1);
        }

        mImageView.setImageDrawable(new BitmapDrawable(bitmap1));

    }

    /**
     * Scaled image width is an integer multiple of 8 and can be ignored
     */
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    public void onClick(View view) {
            if(mTextView6.getText().toString() == "Não") {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("Imagem\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printBitmap(bitmap);
                TectoySunmiPrint.getInstance().print3Line();

            }else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("Imagem\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printBitmap(bitmap);
                TectoySunmiPrint.getInstance().print3Line();
                TectoySunmiPrint.getInstance().cutpaper();
            }
        }
    }


