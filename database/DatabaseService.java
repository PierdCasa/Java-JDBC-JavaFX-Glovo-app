package database;

public class DatabaseService {

    private static DatabaseService instance;

    private final LocationDAO locationDAO;
    private final WalletDAO walletDAO;
    private final UserDAO userDAO;
    private final CategoryDAO categoryDAO;
    private final IngredientDAO ingredientDAO;
    private final ProductDAO productDAO;
    private final RestaurantDAO restaurantDAO;
    private final GroceryStoreDAO groceryStoreDAO;
    private final PromoCodeDAO promoCodeDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final TransactionDAO transactionDAO;
    private final ReviewDAO reviewDAO;
    private final RatingDAO ratingDAO;

    private DatabaseService() {
        this.locationDAO = LocationDAO.getInstance();
        this.walletDAO = WalletDAO.getInstance();
        this.userDAO = UserDAO.getInstance();
        this.categoryDAO = CategoryDAO.getInstance();
        this.ingredientDAO = IngredientDAO.getInstance();
        this.productDAO = ProductDAO.getInstance();
        this.restaurantDAO = RestaurantDAO.getInstance();
        this.groceryStoreDAO = GroceryStoreDAO.getInstance();
        this.promoCodeDAO = PromoCodeDAO.getInstance();
        this.orderDAO = OrderDAO.getInstance();
        this.orderItemDAO = OrderItemDAO.getInstance();
        this.transactionDAO = TransactionDAO.getInstance();
        this.reviewDAO = ReviewDAO.getInstance();
        this.ratingDAO = RatingDAO.getInstance();
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public LocationDAO getLocationDAO() { return locationDAO; }
    public WalletDAO getWalletDAO() { return walletDAO; }
    public UserDAO getUserDAO() { return userDAO; }
    public CategoryDAO getCategoryDAO() { return categoryDAO; }
    public IngredientDAO getIngredientDAO() { return ingredientDAO; }
    public ProductDAO getProductDAO() { return productDAO; }
    public RestaurantDAO getRestaurantDAO() { return restaurantDAO; }
    public GroceryStoreDAO getGroceryStoreDAO() { return groceryStoreDAO; }
    public PromoCodeDAO getPromoCodeDAO() { return promoCodeDAO; }
    public OrderDAO getOrderDAO() { return orderDAO; }
    public OrderItemDAO getOrderItemDAO() { return orderItemDAO; }
    public TransactionDAO getTransactionDAO() { return transactionDAO; }
    public ReviewDAO getReviewDAO() { return reviewDAO; }
    public RatingDAO getRatingDAO() { return ratingDAO; }

    public void closeConnection() {
        DatabaseConnection.getInstance().closeConnection();
    }
}
