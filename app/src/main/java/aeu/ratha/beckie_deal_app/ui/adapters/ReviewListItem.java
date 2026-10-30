package aeu.ratha.beckie_deal_app.ui.adapters;

import java.util.Objects;

public abstract class ReviewListItem {

    public static class Header extends ReviewListItem {
        public final String title;

        public Header(String title) {
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return Objects.equals(title, header.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title);
        }
    }

    public static class Pending extends ReviewListItem {
        public final String id;
        public final String productId;
        public final String name;
        public final String delivered;
        public final String image;

        public Pending(String id, String productId, String name, String delivered, String image) {
            this.id = id;
            this.productId = productId;
            this.name = name;
            this.delivered = delivered;
            this.image = image;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pending pending = (Pending) o;
            return Objects.equals(id, pending.id) &&
                    Objects.equals(productId, pending.productId) &&
                    Objects.equals(name, pending.name) &&
                    Objects.equals(delivered, pending.delivered) &&
                    Objects.equals(image, pending.image);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, productId, name, delivered, image);
        }
    }

    public static class ReviewItem extends ReviewListItem {
        public final String id;
        public final String productId;
        public final String name;
        public final float rating;
        public final String comment;
        public final String image;

        public ReviewItem(String id, String productId, String name, float rating, String comment, String image) {
            this.id = id;
            this.productId = productId;
            this.name = name;
            this.rating = rating;
            this.comment = comment;
            this.image = image;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReviewItem that = (ReviewItem) o;
            return Float.compare(that.rating, rating) == 0 &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(productId, that.productId) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(comment, that.comment) &&
                    Objects.equals(image, that.image);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, productId, name, rating, comment, image);
        }
    }
}
