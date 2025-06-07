CREATE TABLE "Buyer"(
    "id" INTEGER NOT NULL,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "Buyer" ADD PRIMARY KEY("id");
CREATE TABLE "Seller"(
    "id" INTEGER NOT NULL,
    "company_name" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "Seller" ADD PRIMARY KEY("id");
CREATE TABLE "Car"(
    "id" INTEGER NOT NULL,
    "model" VARCHAR(255) NOT NULL,
    "year" VARCHAR(255) NOT NULL,
    "price" DECIMAL(8, 2) NOT NULL,
    "isAvailable" BOOLEAN NOT NULL,
    "seller_id" INTEGER NOT NULL
);
ALTER TABLE
    "Car" ADD PRIMARY KEY("id");
CREATE INDEX "car_seller_id_index" ON
    "Car"("seller_id");
CREATE TABLE "Car_image"(
    "id" INTEGER NOT NULL,
    "car_id" INTEGER NOT NULL,
    "public_id" VARCHAR(255) NOT NULL,
    "image_url" TEXT NOT NULL
);
ALTER TABLE
    "Car_image" ADD PRIMARY KEY("id");
CREATE INDEX "car_image_car_id_index" ON
    "Car_image"("car_id");
CREATE TABLE "Review"(
    "id" INTEGER NOT NULL,
    "car_id" INTEGER NOT NULL,
    "rate" INTEGER NOT NULL,
    "feedback" TEXT NOT NULL,
    "buyer_id" INTEGER NOT NULL
);
ALTER TABLE
    "Review" ADD PRIMARY KEY("id");
CREATE INDEX "review_car_id_index" ON
    "Review"("car_id");
CREATE INDEX "review_buyer_id_index" ON
    "Review"("buyer_id");
CREATE TABLE "User"(
    "id" INTEGER NOT NULL,
    "user_name" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "role" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "User" ADD PRIMARY KEY("id");
CREATE INDEX "user_user_name_index" ON
    "User"("user_name");
CREATE TABLE "purchased_car"(
    "id" INTEGER NOT NULL,
    "buyer_id" INTEGER NOT NULL,
    "seller_id" INTEGER NOT NULL,
    "car_id" INTEGER NOT NULL,
    "purchased_date" DATE NOT NULL
);
ALTER TABLE
    "purchased_car" ADD PRIMARY KEY("id");
ALTER TABLE
    "User" ADD CONSTRAINT "user_user_name_foreign" FOREIGN KEY("user_name") REFERENCES "Seller"("email");
ALTER TABLE
    "purchased_car" ADD CONSTRAINT "purchased_car_buyer_id_foreign" FOREIGN KEY("buyer_id") REFERENCES "Buyer"("id");
ALTER TABLE
    "User" ADD CONSTRAINT "user_user_name_foreign" FOREIGN KEY("user_name") REFERENCES "Buyer"("email");
ALTER TABLE
    "Review" ADD CONSTRAINT "review_car_id_foreign" FOREIGN KEY("car_id") REFERENCES "Car"("id");
ALTER TABLE
    "Car_image" ADD CONSTRAINT "car_image_car_id_foreign" FOREIGN KEY("car_id") REFERENCES "Car"("id");
ALTER TABLE
    "Car" ADD CONSTRAINT "car_seller_id_foreign" FOREIGN KEY("seller_id") REFERENCES "Seller"("id");
ALTER TABLE
    "purchased_car" ADD CONSTRAINT "purchased_car_seller_id_foreign" FOREIGN KEY("seller_id") REFERENCES "Seller"("id");
ALTER TABLE
    "purchased_car" ADD CONSTRAINT "purchased_car_car_id_foreign" FOREIGN KEY("car_id") REFERENCES "Car"("id");
ALTER TABLE
    "Review" ADD CONSTRAINT "review_buyer_id_foreign" FOREIGN KEY("buyer_id") REFERENCES "Buyer"("id");