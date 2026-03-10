import { useEffect, useState } from 'react';
import { Users, ShoppingBag, DollarSign, TrendingUp } from 'lucide-react';
import { useAuthStore } from '../stores/authStore';
import { productApi, orderApi } from '../services/api';

export function AdminDashboardPage() {
  const { user } = useAuthStore();
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalOrders: 0,
    totalRevenue: 0,
    pendingOrders: 0,
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!user || user.role !== 'ADMIN') return;

    const fetchStats = async () => {
      try {
        // In a real app, these would be separate API calls
        const [productsRes, ordersRes] = await Promise.all([
          productApi.getProducts({ size: 1 }),
          orderApi.getAllOrders(),
        ]);

        setStats({
          totalProducts: productsRes.totalElements,
          totalOrders: ordersRes.length,
          totalRevenue: ordersRes.reduce((sum, o) => sum + o.total, 0),
          pendingOrders: ordersRes.filter((o) => o.status === 'PENDING').length,
        });
      } catch (error) {
        console.error('Failed to fetch stats:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStats();
  }, [user]);

  if (!user || user.role !== 'ADMIN') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Access Denied</h2>
          <p className="text-gray-500">You don't have permission to access this page.</p>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600" />
      </div>
    );
  }

  const statCards = [
    {
      title: 'Total Products',
      value: stats.totalProducts,
      icon: ShoppingBag,
      color: 'bg-blue-500',
    },
    {
      title: 'Total Orders',
      value: stats.totalOrders,
      icon: DollarSign,
      color: 'bg-green-500',
    },
    {
      title: 'Revenue',
      value: `$${stats.totalRevenue.toFixed(2)}`,
      icon: TrendingUp,
      color: 'bg-purple-500',
    },
    {
      title: 'Pending Orders',
      value: stats.pendingOrders,
      icon: Users,
      color: 'bg-orange-500',
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {statCards.map((stat) => (
            <div key={stat.title} className="bg-white rounded-lg shadow-sm p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-gray-500 text-sm">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-1">{stat.value}</p>
                </div>
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <stat.icon className="h-6 w-6 text-white" />
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-semibold mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <a
              href="/admin/products"
              className="p-4 border border-gray-200 rounded-lg hover:border-indigo-500 hover:bg-indigo-50 transition"
            >
              <ShoppingBag className="h-8 w-8 text-indigo-600 mb-2" />
              <h3 className="font-medium">Manage Products</h3>
              <p className="text-sm text-gray-500">Add, edit, or remove products</p>
            </a>
            <a
              href="/admin/orders"
              className="p-4 border border-gray-200 rounded-lg hover:border-indigo-500 hover:bg-indigo-50 transition"
            >
              <DollarSign className="h-8 w-8 text-indigo-600 mb-2" />
              <h3 className="font-medium">Manage Orders</h3>
              <p className="text-sm text-gray-500">View and update order status</p>
            </a>
            <a
              href="/admin/users"
              className="p-4 border border-gray-200 rounded-lg hover:border-indigo-500 hover:bg-indigo-50 transition"
            >
              <Users className="h-8 w-8 text-indigo-600 mb-2" />
              <h3 className="font-medium">Manage Users</h3>
              <p className="text-sm text-gray-500">View and manage user accounts</p>
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
