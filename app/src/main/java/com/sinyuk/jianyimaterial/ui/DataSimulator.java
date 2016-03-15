package com.sinyuk.jianyimaterial.ui;


import android.content.Context;

import com.sinyuk.jianyimaterial.R;

import java.util.Random;

/**
 * Created by Sinyuk on 15.11.27.
 */
public class DataSimulator {

    // 1张
    public static final String[] IMAGES_1_URLS = {
            "http://ww1.sinaimg.cn/mw690/b29e155agw1eyfevklbv2j204q08kweq.jpg"
    };
    //    2  张
    public static final String[] IMAGES_2_URLS = {
            "http://ww1.sinaimg.cn/mw690/69352c30gw1ev3w7ivfh6j20yj1g11ct.jpg",
            "http://ww2.sinaimg.cn/mw690/69352c30gw1ev3w6ewp08j21hi0zh1kx.jpg"

    };

    //    4张
    public static final String[] IMAGES_4_URLS = {
            "http://ww3.sinaimg.cn/mw690/b29e155agw1eyfew5ncdoj209j0b4t93.jpg",
            "http://ww1.sinaimg.cn/mw690/b29e155agw1eyfew4gxwej20e60amgow.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eyfew3f622j20e60amae5.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eyfew1ez6jj20dw0dwjuv.jpg"
    };


    // 6 张
    public static final String[] IMAGES_6_URLS = {
            "http://ww3.sinaimg.cn/mw690/7193507bgw1ei6lusjoj1j21kw11tnpe.jpg",
            "http://ww4.sinaimg.cn/mw690/7193507bgw1ei6luhd2i4j21kw11thdu.jpg",
            "http://ww2.sinaimg.cn/mw690/7193507bgw1ei6lv1i92nj21kw11tqv6.jpg",
            "http://ww4.sinaimg.cn/mw690/7193507bgw1ei6lvrg632j21kw11tb2b.jpg",
            "http://ww4.sinaimg.cn/mw690/7193507bgw1ei6lvdu4t2j21kw11tkjm.jpg",
            "http://ww3.sinaimg.cn/mw690/7193507bgw1ei6lw3uhvnj21kw11tu0y.jpg",
    };

    // 9 张
    public static final String[] IMAGES_9_URLS = {
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyfevzdyg8j206t05kaaf.jpg",
            "http://ww1.sinaimg.cn/mw690/b29e155agw1eyfevyveasj206t05k3yo.jpg",
            "http://ww3.sinaimg.cn/mw690/b29e155agw1eyfevyh70hj206t05kdfy.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyfevxf0khj206t05kglx.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eyfevwwxr4j206t05kwep.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eyfevwccq8j206t05kjrl.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyfevvttplj206t05k74e.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyfevv9bdzj206t05kt8v.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyfevuujimj206t05k3yp.jpg",

    };


    static int nameArrayResId = R.array.user_names;

    static String[] contents = {
            "Sed tristique rutrum suscipit.",
            "Sed elementum viverra lacus, vitae tristique mauris sollicitudin at. Aliquam.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam lacinia ac tellus non sagittis. Aliquam erat.",
            "Pellentesque volutpat euismod magna. Maecenas et ligula at ligula fermentum iaculis sed sed libero. Integer gravida ligula in massa mollis.",
            "Nullam a lacinia lectus. Praesent elit orci, tincidunt ut lacinia vel, vulputate eget lacus. Praesent eu lacus at elit efficitur aliquam." +
                    " Quisque eleifend orci odio, et finibus lectus sodales ac. Duis at.",
            "Aenean ultricies felis vulputate lobortis fringilla. Duis nec mauris tempus, vulputate mi ac, elementum diam. Suspendisse in convallis " +
                    "nunc. Aliquam ornare, enim a tincidunt aliquam, ante nulla tempor tortor, in fringilla dolor augue a enim. Donec aliquet tellus" +
                    " sit amet metus vulputate mattis. Suspendisse nec turpis interdum, ultricies urna vel, porta lorem. Maecenas porta id enim in" +
                    " porttitor. Aliquam sapien sapien, viverra sed pulvinar."
    };


    public static String getRandomName(Context context) {
        String[] array = context.getResources().getStringArray(nameArrayResId);
        int random = new Random().nextInt(array.length);
        if (array[random] != null) {
            return array[random];
        } else {
            return "未知";
        }
    }


    public static String getNameByIndex(Context context, int index) {
        String[] array = context.getResources().getStringArray(nameArrayResId);
        int i = index % array.length;
        return array[i];
    }

    public static int getNameArrayresId() {
        return nameArrayResId;
    }

    public static String getRandomContents() {
        int random = new Random().nextInt(6);
        if (contents[random] != null) {
            return contents[random];
        } else {
            return "      ";
        }
    }

    public static String getContentsByIndex(int index) {
        int i = index % contents.length;
        return contents[i];
    }


    public static String getImageUriByIndex(int position) {
        int i = position % IMAGES_9_URLS.length;
        return IMAGES_9_URLS[i];
    }


    public static String[] avatarUrls = new String[]{
            "http://ww2.sinaimg.cn/square/b29e155agw1eylork6523j20zk0npjvg.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eylorl860fj21hc0u078o.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eylork32y7j211y0lcju0.jpg",
            "http://ww1.sinaimg.cn/square/b29e155agw1eylore72c5j20c80c8jst.jpg",
            "http://ww3.sinaimg.cn/square/b29e155agw1eylordr8lzj20dl0h6dh9.jpg",};

    public static String getRandomAvatarUrl() {
        int index = new Random().nextInt(5);
        return avatarUrls[index];
    }

    public static String[] userNames = new String[]{
            "Allen Iverson",
            "G-Dragon",
            "Nate Ruess",
            "Taylor Swift",
            "Sandara Park"
    };
    public static int[] followingStates = new int[]{
            1,
            0,
            0,
            1,
            1
    };
    public static String[] pubDates = new String[]{
            "刚刚",
            "今天 20:01",
            "今天 19:30",
            "昨天 14:50",
            "12.2 22:03",
    };
    public static int[] repoStates = new int[]{
            1,
            0,
            0,
            1,
            1
    };
    public static String[] repoFroms = new String[]{
            "NBA",
            "BigBang",
            "Fun",
            "Shots",
            "2NE1"
    };
    public static String[] textContents = new String[]{
            "Sed tristique rutrum suscipit.",
            "Sed elementum viverra lacus, vitae tristique mauris sollicitudin at. Aliquam.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam lacinia ac tellus non sagittis. Aliquam erat.",
            "Pellentesque volutpat euismod magna. Maecenas et ligula at ligula fermentum iaculis sed sed libero. Integer gravida ligula in massa mollis.",
            "Nullam a lacinia lectus. Praesent elit orci, tincidunt ut lacinia vel, vulputate eget lacus. Praesent eu lacus at elit efficitur aliquam." +
                    " Quisque eleifend orci odio, et finibus lectus sodales ac. Duis at.",
            "Aenean ultricies felis vulputate lobortis fringilla. Duis nec mauris tempus, vulputate mi ac, elementum diam. Suspendisse in convallis " +
                    "nunc. Aliquam ornare, enim a tincidunt aliquam, ante nulla tempor tortor, in fringilla dolor augue a enim. Donec aliquet tellus" +
                    " sit amet metus vulputate mattis. Suspendisse nec turpis interdum, ultricies urna vel, porta lorem. Maecenas porta id enim in" +
                    " porttitor. Aliquam sapien sapien, viverra sed pulvinar."
    };
    public static String[] imageUris = new String[]{
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eylorlm2bsj20go0a03zl.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eylorm2hlgj20e309aq40.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eylormjv5bj20dw0dw0to.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eylormoevbj20dw0kut9r.jpg",
            "http://ww1.sinaimg.cn/mw690/b29e155agw1eylornoxd6j20sg0izq6b.jpg"
    };
    public static String[] backdropUris = new String[]{
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eyloriniegj21270lcqfs.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eylorj38gij20ci0cit92.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eylork32y7j211y0lcju0.jpg",
            "http://ww2.sinaimg.cn/mw690/b29e155agw1eylork6523j20zk0npjvg.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eylorl860fj21hc0u078o.jpg"
    };
    public static String[] bios = new String[]{};

}
