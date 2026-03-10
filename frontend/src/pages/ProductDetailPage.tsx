import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Star, Minus, Plus, ShoppingCart, Check } from 'lucide-react';
import { useProductStore } from '../stores/productStore';
import { useCartStore } from '../stores/cartStore';
import { useAuthStore } from '../stores/authStore';

export function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { currentProduct, fetchProduct, isLoading } = useProductStore();
  const { addToCart } = useCartStore();
  const { user } = useAuthStore();

  const [quantity, setQuantity] = useState(1);
  const [addedToCart, setAddedToCart] = useState(false);
  const [activeTab, setActiveTab] = useState<'description' | 'reviews'>('description');

  useEffect(() => {
    if (id) {
      fetchProduct(Number(id));
    }
    return () => useProductStore.getState().clearCurrentProduct();
  }, [id]);

  const product = currentProduct;

  const handleAddToCart = async () => {
    if (!product) return;
    try {
      await addToCart(product.id, quantity);
      setAddedToCart(true);
      setTimeout(() => setAddedToCart(false), 2000);
    } catch (error) {
      console.error('Failed to add to cart:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600" />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Product Not Found</h2>
          <Link to="/products" className="text-indigo-600 hover:text-indigo-700">
            Back to Products
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <nav className="text-sm mb-6">
          <ol className="flex items-center space-x-2">
            <li>
              <Link to="/" className="text-gray-500 hover:text-gray-700">
                Home
              </Link>
            </li>
            <li className="text-gray-400">/</li>
            <li>
              <Link to="/products" className="text-gray-500 hover:text-gray-700">
                Products
              </Link>
            </li>
            <li className="text-gray-400">/</li>
            <li className="text-gray-900">{product.name}</li>
          </ol>
        </nav>

        {/* Product Details */}
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <div className="grid md:grid-cols-2 gap-8 p-6">
            {/* Product Image */}
            <div className="aspect-square overflow-hidden rounded-lg bg-gray-100">
              <img
                src={product.imageUrl || '/placeholder.jpg'}
                alt={product.name}
                className="w-full h-full object-cover"
              />
            </div>

            {/* Product Info */}
            <div className="flex flex-col">
              <div className="flex items-center space-x-2 mb-2">
                <span className="px-2 py-1 bg-indigo-100 text-indigo-600 text-xs font-medium rounded">
                  {product.category}
                </span>
                {product.stock <= 5 && product.stock > 0 && (
                  <span className="px-2 py-1 bg-orange-100 text-orange-600 text-xs font-medium rounded">
                    Only {product.stock} left!
                  </span>
                )}
                {product.stock === 0 && (
                  <span className="px-2 py-1 bg-red-100 text-red-600 text-xs font-medium rounded">
                    Out of Stock
                  </span>
                )}
              </div>

              <h1 className="text-3xl font-bold text-gray-900 mb-2">{product.name}</h1>

              <div className="flex items-center space-x-2 mb-4">
                <div className="flex items-center">
                  {[...Array(5)].map((_, i) => (
                    <Star
                      key={i}
                      className={`h-5 w-5 ${
                        i < 4 ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
                      }`}
                    />
                  ))}
                </div>
                <span className="text-sm text-gray-500">(4.5) · 128 reviews</span>
              </div>

              <p className="text-3xl font-bold text-indigo-600 mb-4">
                ${product.price.toFixed(2)}
              </p>

              <p className="text-gray-600 mb-6">{product.description}</p>

              {/* Quantity Selector */}
              <div className="flex items-center space-x-4 mb-6">
                <span className="text-gray-700 font-medium">Quantity:</span>
                <div className="flex items-center border border-gray-300 rounded-lg">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-3 py-2 hover:bg-gray-100 transition"
                  >
                    <Minus className="h-4 w-4" />
                  </button>
                  <span className="px-4 py-2 border-x border-gray-300 min-w-[60px] text-center">
                    {quantity}
                  </span>
                  <button
                    onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}
                    disabled={quantity >= product.stock}
                    className="px-3 py-2 hover:bg-gray-100 transition disabled:opacity-50"
                  >
                    <Plus className="h-4 w-4" />
                  </button>
                </div>
              </div>

              {/* Add to Cart Button */}
              <button
                onClick={handleAddToCart}
                disabled={product.stock === 0 || !user}
                className={`w-full py-3 rounded-lg font-semibold flex items-center justify-center space-x-2 transition ${
                  addedToCart
                    ? 'bg-green-600 text-white'
                    : 'bg-indigo-600 text-white hover:bg-indigo-700'
                } disabled:bg-gray-300 disabled:cursor-not-allowed`}
              >
                {addedToCart ? (
                  <>
                    <Check className="h-5 w-5" />
                    <span>Added to Cart!</span>
                  </>
                ) : (
                  <>
                    <ShoppingCart className="h-5 w-5" />
                    <span>{!user ? 'Login to Add' : 'Add to Cart'}</span>
                  </>
                )}
              </button>

              {!user && (
                <p className="text-center text-sm text-gray-500 mt-2">
                  <Link to="/login" className="text-indigo-600 hover:text-indigo-700">
                    Login
                  </Link>
                  {' '}to add items to your cart
                </p>
              )}
            </div>
          </div>

          {/* Tabs */}
          <div className="border-t">
            <div className="flex border-b">
              <button
                onClick={() => setActiveTab('description')}
                className={`px-6 py-3 font-medium transition ${
                  activeTab === 'description'
                    ? 'border-b-2 border-indigo-600 text-indigo-600'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                Description
              </button>
              <button
                onClick={() => setActiveTab('reviews')}
                className={`px-6 py-3 font-medium transition ${
                  activeTab === 'reviews'
                    ? 'border-b-2 border-indigo-600 text-indigo-600'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                Reviews
              </button>
            </div>

            <div className="p-6">
              {activeTab === 'description' ? (
                <div className="prose max-w-none">
                  <h3 className="text-lg font-semibold mb-2">Product Description</h3>
                  <p className="text-gray-600">{product.description}</p>
                  <ul className="list-disc list-inside text-gray-600 mt-4 space-y-2">
                    <li>Premium quality materials</li>
                    <li>Designed for durability and comfort</li>
                    <li>Perfect for everyday use</li>
                    <li>30-day money-back guarantee</li>
                  </ul>
                </div>
              ) : (
                <div>
                  <h3 className="text-lg font-semibold mb-4">Customer Reviews</h3>
                  <div className="space-y-4">
                    {/* Sample reviews - in production, these would come from the API */}
                    {[
                      { name: 'John D.', rating: 5, comment: 'Great product! Exactly as described.' },
                      { name: 'Sarah M.', rating: 4, comment: 'Good quality, fast shipping.' },
                      { name: 'Mike R.', rating: 5, comment: 'Exceeded my expectations!' },
                    ].map((review, i) => (
                      <div key={i} className="bg-gray-50 rounded-lg p-4">
                        <div className="flex items-center justify-between mb-2">
                          <span className="font-medium">{review.name}</span>
                          <div className="flex">
                            {[...Array(5)].map((_, j) => (
                              <Star
                                key={j}
                                className={`h-4 w-4 ${
                                  j < review.rating
                                    ? 'fill-yellow-400 text-yellow-400'
                                    : 'text-gray-300'
                                }`}
                              />
                            ))}
                          </div>
                        </div>
                        <p className="text-gray-600">{review.comment}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
