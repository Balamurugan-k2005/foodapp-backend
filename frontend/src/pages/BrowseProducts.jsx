import React, { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";

const BrowseProducts = () => {
  const { addToCart, cart } = useCart();
  const { user, isAdmin } = useAuth();

  // Products and Categories states
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  // Search & Filtering states
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [sortBy, setSortBy] = useState("name,asc");

  // Pagination states
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [addingId, setAddingId] = useState(null);
  const [successMsg, setSuccessMsg] = useState("");

  // Debouncing Search Input
  const [debouncedSearch, setDebouncedSearch] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  // Helper to flatten category hierarchy for the select dropdown
  const getFlattenedCategories = (cats) => {
    const flat = [];
    const flatten = (list, prefix = "") => {
      list.forEach((cat) => {
        flat.push({
          id: cat.id,
          name: prefix + cat.name,
          slug: cat.slug,
        });
        if (cat.subCategories && cat.subCategories.length > 0) {
          flatten(cat.subCategories, prefix + "— ");
        }
      });
    };
    flatten(cats);
    return flat;
  };

  // Load Categories on mount
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const res = await axiosInstance.get("/api/categories");
        if (res.data && res.data.success) {
          setCategories(res.data.data);
        }
      } catch (err) {
        console.error("Failed to load categories:", err);
      }
    };
    loadCategories();
  }, []);

  // Fetch Products whenever filters, search, sort, or page changes
  useEffect(() => {
    const loadProducts = async () => {
      setLoading(true);
      try {
        const params = {
          page,
          size: 9,
          sort: sortBy,
        };

        if (debouncedSearch) params.search = debouncedSearch;
        if (selectedCategory) params.category = selectedCategory;
        if (minPrice) params.minPrice = minPrice;
        if (maxPrice) params.maxPrice = maxPrice;

        const res = await axiosInstance.get("/api/products", { params });
        if (res.data && res.data.success) {
          setProducts(res.data.data.content);
          setTotalPages(res.data.data.totalPages);
        }
      } catch (err) {
        console.error("Failed to load products:", err);
      } finally {
        setLoading(false);
      }
    };
    loadProducts();
  }, [debouncedSearch, selectedCategory, minPrice, maxPrice, sortBy, page]);

  // Reset page to 0 when filters change
  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, selectedCategory, minPrice, maxPrice, sortBy]);

  const handleAddToCart = async (productId) => {
    if (!user) {
      alert("Please log in to purchase items.");
      return;
    }
    setAddingId(productId);
    setSuccessMsg("");
    try {
      await addToCart(productId, 1);
      setSuccessMsg("Product added to cart!");
      setTimeout(() => setSuccessMsg(""), 2000);
    } catch (err) {
      alert(err.message || "Failed to add item to cart");
    } finally {
      setAddingId(null);
    }
  };

  return (
    <div className="container py-4">
      {/* Toast Alert overlay for cart success */}
      {successMsg && (
        <div
          className="position-fixed bottom-0 end-0 p-3"
          style={{ zIndex: 1055 }}
        >
          <div
            className="alert alert-success border-0 shadow-lg d-flex align-items-center py-2 px-3 gap-2"
            role="alert"
            style={{ borderRadius: "8px" }}
          >
            <i className="bi bi-check-circle-fill text-success fs-5"></i>
            <span className="fw-semibold small">{successMsg}</span>
          </div>
        </div>
      )}

      {/* Catalog Search & Banner */}
      <div
        className="p-4 p-md-5 mb-4 rounded-4 text-white btn-gradient-dark d-flex flex-column justify-content-center align-items-center text-center position-relative overflow-hidden"
        style={{ minHeight: "220px" }}
      >
        <h1 className="display-5 fw-bold z-1">Discover Next-Gen Technology</h1>
        <p className="lead z-1 text-light small" style={{ maxWidth: "600px" }}>
          Get up to 10% discount using exclusive checkout coupons. Fast
          shipping, secure payments, and active product support.
        </p>

        {/* Search Bar */}
        <div className="w-100 mt-3 z-1" style={{ maxWidth: "500px" }}>
          <div className="input-group bg-white rounded-pill p-1 shadow-sm">
            <span className="input-group-text bg-white border-0">
              <i className="bi bi-search text-muted"></i>
            </span>
            <input
              type="text"
              className="form-control border-0 py-2 bg-white text-dark small"
              placeholder="Search gadgets, smartphones, accessories..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ boxShadow: "none" }}
            />
          </div>
        </div>
      </div>

      <div className="row g-4">
        {/* Sidebar Filters */}
        <div className="col-lg-3">
          <div
            className="card border-0 shadow-sm p-4 rounded-3 sticky-lg-top"
            style={{ top: "90px" }}
          >
            <h5 className="fw-bold mb-3 d-flex align-items-center gap-2">
              <i className="bi bi-funnel text-primary"></i>Filters
            </h5>

            {/* Category selection */}
            <div className="mb-4">
              <label className="form-label small fw-semibold text-muted">
                Category
              </label>
              <select
                className="form-select border-0 bg-light py-2 rounded-3 text-dark small"
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
              >
                <option value="">All Categories</option>
                {getFlattenedCategories(categories).map((cat) => (
                  <option key={cat.id} value={cat.slug}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Price ranges */}
            <div className="mb-4">
              <label className="form-label small fw-semibold text-muted">
                Price Range ($)
              </label>
              <div className="d-flex align-items-center gap-2">
                <input
                  type="number"
                  className="form-control border-0 bg-light py-2 rounded-3 small"
                  placeholder="Min"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                />
                <span className="text-muted small">-</span>
                <input
                  type="number"
                  className="form-control border-0 bg-light py-2 rounded-3 small"
                  placeholder="Max"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                />
              </div>
            </div>

            {/* Sorting criteria */}
            <div className="mb-4">
              <label className="form-label small fw-semibold text-muted">
                Sort By
              </label>
              <select
                className="form-select border-0 bg-light py-2 rounded-3 text-dark small"
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <option value="name,asc">Alphabetical (A - Z)</option>
                <option value="name,desc">Alphabetical (Z - A)</option>
                <option value="price,asc">Price: Low to High</option>
                <option value="price,desc">Price: High to Low</option>
                <option value="createdAt,desc">Newest Arrivals</option>
              </select>
            </div>

            {/* Reset Filters button */}
            <button
              onClick={() => {
                setSearch("");
                setSelectedCategory("");
                setMinPrice("");
                setMaxPrice("");
                setSortBy("name,asc");
              }}
              className="btn btn-outline-secondary w-100 py-2 rounded-pill small fw-semibold"
            >
              Reset Filters
            </button>
          </div>
        </div>

        {/* Catalog Grid */}
        <div className="col-lg-9">
          {loading ? (
            <div className="row g-4">
              {[1, 2, 3, 4, 5, 6].map((skel) => (
                <div key={skel} className="col-md-4 col-sm-6">
                  <div
                    className="card border-0 shadow-sm p-3 rounded-3"
                    style={{ height: "380px" }}
                  >
                    <div className="placeholder-glow h-50 bg-light rounded-3 mb-3"></div>
                    <div className="placeholder-glow">
                      <span className="placeholder col-6 bg-light mb-2"></span>
                      <span className="placeholder col-8 bg-light mb-3"></span>
                      <span className="placeholder col-4 bg-light"></span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : products.length === 0 ? (
            <div className="text-center py-5">
              <i className="bi bi-search text-muted display-1"></i>
              <h3 className="fw-bold text-dark mt-3">No Products Found</h3>
              <p className="text-muted small">
                We couldn't find any products matching your filters.
              </p>
            </div>
          ) : (
            <>
              <div className="row g-4">
                {products.map((prod) => (
                  <div
                    key={prod.id}
                    className="col-md-4 col-sm-6 d-flex align-items-stretch"
                  >
                    <div className="card hover-card border-0 shadow-sm p-3 w-100 d-flex flex-column justify-content-between">
                      <div>
                        {/* Image section */}
                        <div
                          className="text-center bg-light rounded-3 p-3 mb-3 position-relative"
                          style={{ height: "180px", overflow: "hidden" }}
                        >
                          <img
                            src={
                              prod.imageUrl ||
                              "https://via.placeholder.com/200x200?text=No+Image"
                            }
                            alt={prod.name}
                            className="img-fluid h-100 object-fit-contain"
                            style={{ mixBlendMode: "multiply" }}
                          />
                          {prod.stock === 0 ? (
                            <span className="position-absolute top-0 start-0 m-2 badge bg-danger rounded-pill">
                              Out of Stock
                            </span>
                          ) : prod.stock <= 5 ? (
                            <span className="position-absolute top-0 start-0 m-2 badge bg-warning text-dark rounded-pill">
                              Only {prod.stock} left!
                            </span>
                          ) : null}
                        </div>

                        {/* Title and details link */}
                        <span className="badge bg-light text-muted mb-2 small">
                          {prod.categoryName || "General"}
                        </span>
                        <Link
                          to={`/products/${prod.id}`}
                          className="text-decoration-none"
                        >
                          <h6 className="fw-bold text-dark text-truncate mb-1">
                            {prod.name}
                          </h6>
                        </Link>

                        {/* Reviews & Average Rating */}
                        <div className="d-flex align-items-center mb-2 small text-warning">
                          <i className="bi bi-star-fill me-1"></i>
                          <span className="text-dark fw-bold small me-1">
                            {prod.averageRating
                              ? prod.averageRating.toFixed(1)
                              : "5.0"}
                          </span>
                          <span className="text-muted small">
                            (
                            {prod.reviewsCount !== undefined
                              ? prod.reviewsCount
                              : "0"}
                            )
                          </span>
                        </div>

                        {/* Description */}
                        <p
                          className="text-muted small text-truncate-2 mb-3"
                          style={{
                            height: "36px",
                            overflow: "hidden",
                            display: "-webkit-box",
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: "vertical",
                          }}
                        >
                          {prod.description ||
                            "No description available for this premium tech item."}
                        </p>
                      </div>

                      {/* Add-to-cart or out of stock action */}
                      <div className="d-flex align-items-center justify-content-between mt-auto">
                        <span className="fs-5 fw-bold text-dark">
                          ${prod.price.toFixed(2)}
                        </span>
                        {!isAdmin && (
                          <button
                            onClick={() => handleAddToCart(prod.id)}
                            disabled={prod.stock === 0 || addingId === prod.id}
                            className="btn btn-gradient-primary rounded-pill px-3 py-1 fw-semibold small d-flex align-items-center gap-1"
                          >
                            {addingId === prod.id ? (
                              <span
                                className="spinner-border spinner-border-sm"
                                role="status"
                                aria-hidden="true"
                              ></span>
                            ) : (
                              <>
                                <i className="bi bi-cart-plus"></i>
                                <span>Add</span>
                              </>
                            )}
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination controls */}
              {totalPages > 1 && (
                <div className="d-flex justify-content-center mt-5">
                  <nav>
                    <ul className="pagination gap-1 border-0">
                      <li
                        className={`page-item ${page === 0 ? "disabled" : ""}`}
                      >
                        <button
                          className="page-link rounded-pill border-0 shadow-sm bg-white"
                          onClick={() => setPage(page - 1)}
                        >
                          <i className="bi bi-chevron-left"></i>
                        </button>
                      </li>
                      {[...Array(totalPages)].map((_, i) => (
                        <li
                          key={i}
                          className={`page-item ${page === i ? "active" : ""}`}
                        >
                          <button
                            className={`page-link rounded-pill border-0 shadow-sm ${page === i ? "bg-primary text-white" : "bg-white text-dark"}`}
                            onClick={() => setPage(i)}
                          >
                            {i + 1}
                          </button>
                        </li>
                      ))}
                      <li
                        className={`page-item ${page === totalPages - 1 ? "disabled" : ""}`}
                      >
                        <button
                          className="page-link rounded-pill border-0 shadow-sm bg-white"
                          onClick={() => setPage(page + 1)}
                        >
                          <i className="bi bi-chevron-right"></i>
                        </button>
                      </li>
                    </ul>
                  </nav>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default BrowseProducts;
