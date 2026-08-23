package com.ecommerce.app;

import com.ecommerce.app.entity.*;
import com.ecommerce.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public DatabaseSeeder(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          ReviewRepository reviewRepository,
                          CouponRepository couponRepository,
                          AddressRepository addressRepository,
                          PasswordEncoder passwordEncoder,
                          RestaurantRepository restaurantRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.couponRepository = couponRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Clear old database records to avoid conflicts
        System.out.println(">>> Clearing old database records for food delivery schema...");
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        restaurantRepository.deleteAll();

        System.out.println(">>> Seeding Food Delivery database with fresh, beautiful sample data...");

        // 1. Seed Users
        Map<String, User> seededUsers = seedUsers();

        // 2. Seed Addresses
        seedAddresses(seededUsers);

        // 3. Seed Coupons
        seedCoupons();

        // 4. Seed Restaurants
        Map<String, Restaurant> seededRestaurants = seedRestaurants(seededUsers);

        // 5. Seed Food Categories
        Map<String, Category> seededCategories = seedCategories();

        // 6. Seed Dishes (Products)
        List<Product> seededProducts = seedProducts(seededCategories, seededRestaurants);

        // 7. Seed Reviews
        seedReviews(seededUsers, seededProducts);

        System.out.println(">>> Food Delivery database seeding COMPLETED successfully!");
    }

    private Map<String, User> seedUsers() {
        Map<String, User> userMap = new HashMap<>();

        // Admin User
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567890")
                    .role(Role.ADMIN)
                    .build();
            userMap.put("admin", userRepository.save(admin));
        } else {
            userMap.put("admin", userRepository.findByEmail("admin@ecommerce.com").get());
        }

        // Restaurant Owner User 1
        if (!userRepository.existsByEmail("owner@ecommerce.com")) {
            User owner = User.builder()
                    .name("Restaurant Owner 1")
                    .email("owner@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567892")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner", userRepository.save(owner));
        } else {
            userMap.put("owner", userRepository.findByEmail("owner@ecommerce.com").get());
        }

        // Restaurant Owner User 2
        if (!userRepository.existsByEmail("owner2@ecommerce.com")) {
            User owner2 = User.builder()
                    .name("Restaurant Owner 2")
                    .email("owner2@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567897")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner2", userRepository.save(owner2));
        } else {
            userMap.put("owner2", userRepository.findByEmail("owner2@ecommerce.com").get());
        }

        // Restaurant Owner User 3
        if (!userRepository.existsByEmail("owner3@ecommerce.com")) {
            User owner3 = User.builder()
                    .name("Restaurant Owner 3")
                    .email("owner3@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567898")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner3", userRepository.save(owner3));
        } else {
            userMap.put("owner3", userRepository.findByEmail("owner3@ecommerce.com").get());
        }

        // Restaurant Owner User 4
        if (!userRepository.existsByEmail("owner4@ecommerce.com")) {
            User owner4 = User.builder()
                    .name("Restaurant Owner 4")
                    .email("owner4@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567899")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner4", userRepository.save(owner4));
        } else {
            userMap.put("owner4", userRepository.findByEmail("owner4@ecommerce.com").get());
        }

        // Restaurant Owner User 5
        if (!userRepository.existsByEmail("owner5@ecommerce.com")) {
            User owner5 = User.builder()
                    .name("Restaurant Owner 5")
                    .email("owner5@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567831")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner5", userRepository.save(owner5));
        } else {
            userMap.put("owner5", userRepository.findByEmail("owner5@ecommerce.com").get());
        }

        // Restaurant Owner User 6
        if (!userRepository.existsByEmail("owner6@ecommerce.com")) {
            User owner6 = User.builder()
                    .name("Restaurant Owner 6")
                    .email("owner6@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567832")
                    .role(Role.RESTAURANT_OWNER)
                    .build();
            userMap.put("owner6", userRepository.save(owner6));
        } else {
            userMap.put("owner6", userRepository.findByEmail("owner6@ecommerce.com").get());
        }

        // Customer User
        if (!userRepository.existsByEmail("customer@ecommerce.com")) {
            User customer = User.builder()
                    .name("Customer User")
                    .email("customer@ecommerce.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("1234567891")
                    .role(Role.CUSTOMER)
                    .build();
            userMap.put("customer", userRepository.save(customer));
        } else {
            userMap.put("customer", userRepository.findByEmail("customer@ecommerce.com").get());
        }

        // Bala User (Admin)
        if (!userRepository.existsByEmail("bala@gmail.com")) {
            User bala = User.builder()
                    .name("Bala")
                    .email("bala@gmail.com")
                    .password(passwordEncoder.encode("Password123"))
                    .phone("9999999999")
                    .role(Role.ADMIN)
                    .build();
            userMap.put("bala", userRepository.save(bala));
        } else {
            userMap.put("bala", userRepository.findByEmail("bala@gmail.com").get());
        }

        // Additional Customer Users for Reviews
        String[] customerEmails = {"alice@ecommerce.com", "bob@ecommerce.com", "charlie@ecommerce.com"};
        String[] customerNames = {"Alice Johnson", "Bob Miller", "Charlie Davis"};

        for (int i = 0; i < customerEmails.length; i++) {
            String email = customerEmails[i];
            if (!userRepository.existsByEmail(email)) {
                User extraCustomer = User.builder()
                        .name(customerNames[i])
                        .email(email)
                        .password(passwordEncoder.encode("Password123"))
                        .phone("123456789" + (3 + i))
                        .role(Role.CUSTOMER)
                        .build();
                userMap.put(email.split("@")[0], userRepository.save(extraCustomer));
            } else {
                userMap.put(email.split("@")[0], userRepository.findByEmail(email).get());
            }
        }

        return userMap;
    }

    private void seedAddresses(Map<String, User> users) {
        users.forEach((key, user) -> {
            if (addressRepository.findAll().stream().noneMatch(a -> a.getUser().getId().equals(user.getId()))) {
                Address address = Address.builder()
                        .street("123 Grand Avenue, Apt 4B")
                        .city("New York")
                        .state("NY")
                        .pincode("10001")
                        .country("United States")
                        .isDefault(true)
                        .user(user)
                        .build();
                addressRepository.save(address);
            }
        });
    }

    private void seedCoupons() {
        List<Coupon> coupons = Arrays.asList(
                Coupon.builder()
                        .code("WELCOME10")
                        .discountPercent(10)
                        .expiryDate(LocalDate.now().plusYears(1))
                        .minOrderAmount(BigDecimal.valueOf(300.00))
                        .isActive(true)
                        .build(),
                Coupon.builder()
                        .code("MEGA20")
                        .discountPercent(20)
                        .expiryDate(LocalDate.now().plusYears(1))
                        .minOrderAmount(BigDecimal.valueOf(600.00))
                        .isActive(true)
                        .build(),
                Coupon.builder()
                        .code("SAVER5")
                        .discountPercent(5)
                        .expiryDate(LocalDate.now().plusYears(1))
                        .minOrderAmount(BigDecimal.valueOf(200.00))
                        .isActive(true)
                        .build()
        );

        for (Coupon coupon : coupons) {
            if (couponRepository.findByCode(coupon.getCode()).isEmpty()) {
                couponRepository.save(coupon);
            }
        }
    }

    private Map<String, Restaurant> seedRestaurants(Map<String, User> users) {
        Map<String, Restaurant> restaurantMap = new HashMap<>();
        User owner = users.get("owner");
        User owner2 = users.get("owner2");
        User owner3 = users.get("owner3");
        User owner4 = users.get("owner4");
        User owner5 = users.get("owner5");
        User owner6 = users.get("owner6");

        Restaurant r1 = getOrCreateRestaurant("The Biryani Palace", 
                "Delicious, rich-flavored authentic Dum Biryani cooked to perfection by master chefs.", 
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80", 
                "Biryani, North Indian", 35, BigDecimal.valueOf(350.00), owner);
        restaurantMap.put("palace", r1);

        Restaurant r2 = getOrCreateRestaurant("Pizza Express", 
                "Stone-baked Italian pizzas with fresh mozzarella cheese, tangy tomatoes, and custom toppings.", 
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=600&q=80", 
                "Pizza, Italian", 25, BigDecimal.valueOf(400.00), owner5);
        restaurantMap.put("express", r2);

        Restaurant r3 = getOrCreateRestaurant("Dosa Corner", 
                "Crispy South Indian Dosas, Idlis, and fluffy Vadas served with fresh coconut chutney and sambar.", 
                "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80", 
                "South Indian, Vegetarian", 20, BigDecimal.valueOf(150.00), owner6);
        restaurantMap.put("dosa", r3);

        Restaurant r4 = getOrCreateRestaurant("Burger Junction", 
                "Gourmet flame-grilled burgers, loaded premium fries, and thick delicious milkshakes.", 
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80", 
                "Burgers, Fast Food", 15, BigDecimal.valueOf(250.00), owner2);
        restaurantMap.put("burger", r4);

        Restaurant r5 = getOrCreateRestaurant("Royal Wok", 
                "Traditional Asian stir-fries, spicy Szechuan dishes, and handmade spring rolls.", 
                "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80", 
                "Chinese, Asian", 30, BigDecimal.valueOf(300.00), owner3);
        restaurantMap.put("wok", r5);

        Restaurant r6 = getOrCreateRestaurant("Sweet Treats", 
                "Premium fudge brownies, gourmet cupcakes, red velvet pastries, and ice creams.", 
                "https://images.unsplash.com/photo-1587314168485-3236d6710814?auto=format&fit=crop&w=600&q=80", 
                "Desserts, Sweets", 15, BigDecimal.valueOf(200.00), owner4);
        restaurantMap.put("sweet", r6);

        return restaurantMap;
    }

    private Restaurant getOrCreateRestaurant(String name, String description, String imageUrl, String cuisineType, Integer deliveryTime, BigDecimal averagePrice, User owner) {
        return restaurantRepository.findAll().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> restaurantRepository.save(Restaurant.builder()
                        .name(name)
                        .description(description)
                        .imageUrl(imageUrl)
                        .cuisineType(cuisineType)
                        .deliveryTime(deliveryTime)
                        .averagePrice(averagePrice)
                        .owner(owner)
                        .isActive(true)
                        .build()));
    }

    private Map<String, Category> seedCategories() {
        Map<String, Category> categoryMap = new HashMap<>();

        categoryMap.put("biryani", getOrCreateCategory("Biryani", "biryani", "Delicious flavored rice cooked with meat or veggies", null));
        categoryMap.put("pizza", getOrCreateCategory("Pizza", "pizza", "Cheesy pizzas with custom toppings", null));
        categoryMap.put("burger", getOrCreateCategory("Burger", "burger", "Juicy burgers with crispy patties", null));
        categoryMap.put("south-indian", getOrCreateCategory("South Indian", "south-indian", "Dosa, Idli, Vada, and more", null));
        categoryMap.put("desserts", getOrCreateCategory("Desserts", "desserts", "Sweet treats and ice cream", null));
        categoryMap.put("beverages", getOrCreateCategory("Beverages", "beverages", "Refreshing drinks and shakes", null));

        return categoryMap;
    }

    private Category getOrCreateCategory(String name, String slug, String description, Category parent) {
        return categoryRepository.findBySlug(slug)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .slug(slug)
                        .description(description)
                        .parentCategory(parent)
                        .build()));
    }

    private List<Product> seedProducts(Map<String, Category> categories, Map<String, Restaurant> restaurants) {
        List<Product> productsToSeed = Arrays.asList(
                // Biryani Palace Menu
                Product.builder()
                        .name("Hyderabadi Chicken Biryani")
                        .description("Spicy basmati rice layered with tender marinated chicken, cooked in absolute dum style.")
                        .price(BigDecimal.valueOf(299.00))
                        .stock(100)
                        .imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80")
                        .slug("hyderabadi-chicken-biryani")
                        .category(categories.get("biryani"))
                        .restaurant(restaurants.get("palace"))
                        .build(),
                Product.builder()
                        .name("Special Paneer Biryani")
                        .description("Fragrant basmati rice layered with soft paneer cubes, saffron, and fresh mint leaves.")
                        .price(BigDecimal.valueOf(249.00))
                        .stock(80)
                        .imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80")
                        .slug("special-paneer-biryani")
                        .category(categories.get("biryani"))
                        .restaurant(restaurants.get("palace"))
                        .build(),
                Product.builder()
                        .name("Chicken Tandoori Starter")
                        .description("Juicy chicken thighs marinated in yogurt and spices, grilled inside clay tandoor.")
                        .price(BigDecimal.valueOf(199.00))
                        .stock(50)
                        .imageUrl("https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80")
                        .slug("chicken-tandoori-starter")
                        .category(categories.get("biryani"))
                        .restaurant(restaurants.get("palace"))
                        .build(),

                // Pizza Express Menu
                Product.builder()
                        .name("Margherita Pizza")
                        .description("Classic thin crust topped with signature pizza sauce, extra virgin olive oil, fresh basil, and mozzarella.")
                        .price(BigDecimal.valueOf(349.00))
                        .stock(150)
                        .imageUrl("https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=600&q=80")
                        .slug("margherita-pizza")
                        .category(categories.get("pizza"))
                        .restaurant(restaurants.get("express"))
                        .build(),
                Product.builder()
                        .name("Spicy Pepperoni Pizza")
                        .description("Loaded with pork pepperoni, jalapenos, chili flakes, and hot honey glaze over mozzarella cheese.")
                        .price(BigDecimal.valueOf(449.00))
                        .stock(120)
                        .imageUrl("https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=600&q=80")
                        .slug("spicy-pepperoni-pizza")
                        .category(categories.get("pizza"))
                        .restaurant(restaurants.get("express"))
                        .build(),
                Product.builder()
                        .name("Garlic Breadsticks")
                        .description("Baked dough brushed with garlic butter and fresh herbs, served with marinara dip.")
                        .price(BigDecimal.valueOf(129.00))
                        .stock(200)
                        .imageUrl("https://images.unsplash.com/photo-1544982503-9f984c14501a?auto=format&fit=crop&w=600&q=80")
                        .slug("garlic-breadsticks")
                        .category(categories.get("pizza"))
                        .restaurant(restaurants.get("express"))
                        .build(),

                // Dosa Corner Menu
                Product.builder()
                        .name("Masala Dosa")
                        .description("Crispy thin rice pancake stuffed with spiced potato mash, served with coconut chutney.")
                        .price(BigDecimal.valueOf(99.00))
                        .stock(300)
                        .imageUrl("https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80")
                        .slug("masala-dosa")
                        .category(categories.get("south-indian"))
                        .restaurant(restaurants.get("dosa"))
                        .build(),
                Product.builder()
                        .name("Steamed Idli Sambar")
                        .description("Soft, fluffy steamed rice cakes served hot with mixed vegetable lentil stew.")
                        .price(BigDecimal.valueOf(79.00))
                        .stock(250)
                        .imageUrl("https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80")
                        .slug("steamed-idli-sambar")
                        .category(categories.get("south-indian"))
                        .restaurant(restaurants.get("dosa"))
                        .build(),
                Product.builder()
                        .name("Mango Lassi")
                        .description("Thick sweet yogurt drink blended with fresh ripe mango pulp.")
                        .price(BigDecimal.valueOf(59.00))
                        .stock(150)
                        .imageUrl("https://images.unsplash.com/photo-1571115177098-24ec42095185?auto=format&fit=crop&w=600&q=80")
                        .slug("mango-lassi")
                        .category(categories.get("beverages"))
                        .restaurant(restaurants.get("dosa"))
                        .build(),

                // Burger Junction Menu
                Product.builder()
                        .name("Cheese Burger Delight")
                        .description("Grilled premium beef patty topped with melted cheddar, lettuce, onions, and house sauce.")
                        .price(BigDecimal.valueOf(149.00))
                        .stock(120)
                        .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80")
                        .slug("cheese-burger-delight")
                        .category(categories.get("burger"))
                        .restaurant(restaurants.get("burger"))
                        .build(),
                Product.builder()
                        .name("Crispy Chicken Slider")
                        .description("Buttermilk fried chicken breast topped with crunchy coleslaw and spicy mayo.")
                        .price(BigDecimal.valueOf(119.00))
                        .stock(100)
                        .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80")
                        .slug("crispy-chicken-slider")
                        .category(categories.get("burger"))
                        .restaurant(restaurants.get("burger"))
                        .build(),
                Product.builder()
                        .name("Loaded Premium Fries")
                        .description("Crispy golden french fries loaded with liquid cheese, jalapenos, and spring onions.")
                        .price(BigDecimal.valueOf(99.00))
                        .stock(200)
                        .imageUrl("https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=600&q=80")
                        .slug("loaded-premium-fries")
                        .category(categories.get("burger"))
                        .restaurant(restaurants.get("burger"))
                        .build(),

                // Royal Wok Menu
                Product.builder()
                        .name("Schezwan Fried Rice")
                        .description("Spicy fried basmati rice tossed with fresh exotic vegetables, dark soy, and Schezwan sauce.")
                        .price(BigDecimal.valueOf(179.00))
                        .stock(100)
                        .imageUrl("https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80")
                        .slug("schezwan-fried-rice")
                        .category(categories.get("south-indian"))
                        .restaurant(restaurants.get("wok"))
                        .build(),
                Product.builder()
                        .name("Veg Spring Rolls")
                        .description("Crispy rolled wrappers stuffed with seasoned cabbage, carrots, and glass noodles.")
                        .price(BigDecimal.valueOf(119.00))
                        .stock(150)
                        .imageUrl("https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80")
                        .slug("veg-spring-rolls")
                        .category(categories.get("south-indian"))
                        .restaurant(restaurants.get("wok"))
                        .build(),

                // Sweet Treats Menu
                Product.builder()
                        .name("Chocolate Fudge Brownie")
                        .description("Dense, rich chocolate brownie loaded with chocolate chunks and served warm.")
                        .price(BigDecimal.valueOf(99.00))
                        .stock(80)
                        .imageUrl("https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=600&q=80")
                        .slug("chocolate-fudge-brownie")
                        .category(categories.get("desserts"))
                        .restaurant(restaurants.get("sweet"))
                        .build(),
                Product.builder()
                        .name("Red Velvet Cupcake")
                        .description("Soft red velvet pastry topped with thick cream cheese frosting and sprinkles.")
                        .price(BigDecimal.valueOf(69.00))
                        .stock(100)
                        .imageUrl("https://images.unsplash.com/photo-1587314168485-3236d6710814?auto=format&fit=crop&w=600&q=80")
                        .slug("red-velvet-cupcake")
                        .category(categories.get("desserts"))
                        .restaurant(restaurants.get("sweet"))
                        .build(),
                Product.builder()
                        .name("Strawberry Milkshake")
                        .description("Refreshing creamy milkshake made with fresh strawberries and vanilla ice cream.")
                        .price(BigDecimal.valueOf(89.00))
                        .stock(120)
                        .imageUrl("https://images.unsplash.com/photo-1571115177098-24ec42095185?auto=format&fit=crop&w=600&q=80")
                        .slug("strawberry-milkshake")
                        .category(categories.get("beverages"))
                        .restaurant(restaurants.get("sweet"))
                        .build()
        );

        List<Product> savedProducts = new ArrayList<>();
        for (Product product : productsToSeed) {
            Optional<Product> existing = productRepository.findBySlug(product.getSlug());
            if (existing.isEmpty()) {
                savedProducts.add(productRepository.save(product));
            } else {
                savedProducts.add(existing.get());
            }
        }
        return savedProducts;
    }

    private void seedReviews(Map<String, User> users, List<Product> products) {
        String[] commentsExcellent = {"Tastes absolutely amazing! Fresh and hot.", "Highly recommended, will definitely order again.", "Authentic flavors and great packaging."};
        String[] commentsGood = {"Good portion size and great taste.", "Satisfying meal, arrived warm.", "Worth the price."};

        Random random = new Random();

        for (Product product : products) {
            int reviewsToCreate = random.nextInt(2) + 1;
            List<User> reviewCandidates = new ArrayList<>(users.values());
            reviewCandidates.removeIf(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.RESTAURANT_OWNER);

            Collections.shuffle(reviewCandidates);

            for (int i = 0; i < Math.min(reviewsToCreate, reviewCandidates.size()); i++) {
                User user = reviewCandidates.get(i);

                if (!reviewRepository.existsByUserAndProduct(user, product)) {
                    int rating = random.nextInt(2) + 4; // 4 or 5 stars
                    String comment = rating == 5 ? commentsExcellent[random.nextInt(commentsExcellent.length)]
                            : commentsGood[random.nextInt(commentsGood.length)];

                    reviewRepository.save(Review.builder()
                            .rating(rating)
                            .comment(comment)
                            .user(user)
                            .product(product)
                            .build());
                }
            }
        }
    }
}
