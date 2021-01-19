package com.xsic.xsic.ui.illusionMain.customResponseBody;

import java.util.List;

public class CustomResponseBody {

    /**
     * banner : {"type":1,"contentList":[{"imgUrl":"","jumpType":1,"jumpUrl":"","videoUrl":""},{"imgUrl":"","jumpType":1,"jumpUrl":"","videoUrl":""}]}
     * list : [{"imgUrl":"","title":"","status":1,"jumpType":1,"jumpUrl":"","isNeedDownload":1,"downloadUrl":""}]
     */

    private BannerBean banner;
    private List<ListBean> list;

    public BannerBean getBanner() {
        return banner;
    }

    public void setBanner(BannerBean banner) {
        this.banner = banner;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class BannerBean {
        /**
         * type : 1
         * contentList : [{"imgUrl":"","jumpType":1,"jumpUrl":"","videoUrl":""},{"imgUrl":"","jumpType":1,"jumpUrl":"","videoUrl":""}]
         */

        private int type;
        private List<ContentListBean> contentList;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<ContentListBean> getContentList() {
            return contentList;
        }

        public void setContentList(List<ContentListBean> contentList) {
            this.contentList = contentList;
        }

        public static class ContentListBean {
            /**
             * imgUrl :
             * jumpType : 1
             * jumpUrl :
             * videoUrl :
             */

            private String imgUrl;
            private int jumpType;
            private String jumpUrl;
            private String videoUrl;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public int getJumpType() {
                return jumpType;
            }

            public void setJumpType(int jumpType) {
                this.jumpType = jumpType;
            }

            public String getJumpUrl() {
                return jumpUrl;
            }

            public void setJumpUrl(String jumpUrl) {
                this.jumpUrl = jumpUrl;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }
        }
    }

    public static class ListBean {
        /**
         * imgUrl :
         * title :
         * status : 1
         * jumpType : 1
         * jumpUrl :
         * isNeedDownload : 1
         * downloadUrl :
         */

        private String imgUrl;
        private String title;
        private int status;
        private int jumpType;
        private String jumpUrl;
        private int isNeedDownload;
        private String downloadUrl;
        private int height;
        private int width;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getJumpType() {
            return jumpType;
        }

        public void setJumpType(int jumpType) {
            this.jumpType = jumpType;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public int getIsNeedDownload() {
            return isNeedDownload;
        }

        public void setIsNeedDownload(int isNeedDownload) {
            this.isNeedDownload = isNeedDownload;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
