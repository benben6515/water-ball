'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import api from '@/lib/api';

interface OrderItem {
  orderItemId: number;
  courseId: number;
  courseTitle: string;
  courseCoverImageUrl: string;
  price: number;
}

interface Order {
  orderId: number;
  totalAmount: number;
  paymentStatus: string;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItem[];
}

/**
 * Order History Page
 * Displays user's order history with purchased courses.
 */
export default function OrderHistoryPage() {
  const router = useRouter();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await api.get('/api/orders');
        setOrders(response.data);
        setError(null);
      } catch (err: any) {
        console.error('Failed to fetch orders:', err);
        if (err.response?.status === 401) {
          // Not logged in - redirect to login
          router.push('/login?redirect=/orders');
        } else {
          setError('無法載入訂單記錄，請稍後再試');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [router]);

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getPaymentStatusBadge = (status: string) => {
    switch (status) {
      case 'PAID':
        return <span className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-semibold">已付款</span>;
      case 'PENDING':
        return <span className="px-3 py-1 bg-yellow-100 text-yellow-700 rounded-full text-sm font-semibold">待付款</span>;
      case 'CANCELLED':
        return <span className="px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm font-semibold">已取消</span>;
      case 'REFUNDED':
        return <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm font-semibold">已退款</span>;
      default:
        return <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm font-semibold">{status}</span>;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="flex justify-center items-center py-20">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-red-50 border border-red-200 rounded-lg p-8 text-center">
            <p className="text-red-600 text-lg mb-4">{error}</p>
            <button
              onClick={() => router.push('/courses')}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              返回課程列表
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            訂單記錄
          </h1>
          <p className="text-lg text-gray-600">
            查看您的課程購買記錄
          </p>
        </div>

        {/* Orders List */}
        {orders.length === 0 ? (
          <div className="bg-white rounded-xl shadow-lg p-12 text-center">
            <div className="max-w-md mx-auto">
              <div className="mb-6">
                <svg
                  className="mx-auto h-24 w-24 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1.5}
                    d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
                  />
                </svg>
              </div>
              <h2 className="text-2xl font-semibold text-gray-900 mb-3">
                尚無訂單記錄
              </h2>
              <p className="text-gray-600 mb-6">
                您還沒有購買任何課程
              </p>
              <button
                onClick={() => router.push('/courses')}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold"
              >
                瀏覽課程
              </button>
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div
                key={order.orderId}
                className="bg-white rounded-xl shadow-lg overflow-hidden"
              >
                {/* Order Header */}
                <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
                  <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                    <div>
                      <div className="flex items-center gap-3 mb-2">
                        <h3 className="text-lg font-semibold text-gray-900">
                          訂單編號：#{order.orderId}
                        </h3>
                        {getPaymentStatusBadge(order.paymentStatus)}
                      </div>
                      <p className="text-sm text-gray-600">
                        訂單時間：{formatDate(order.createdAt)}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-600 mb-1">總金額</p>
                      <p className="text-2xl font-bold text-gray-900">
                        NT$ {order.totalAmount.toLocaleString()}
                      </p>
                      {order.paymentMethod && (
                        <p className="text-sm text-gray-500 mt-1">
                          {order.paymentMethod === 'MOCK' ? '模擬付款' : order.paymentMethod}
                        </p>
                      )}
                    </div>
                  </div>
                </div>

                {/* Order Items */}
                <div className="p-6">
                  <h4 className="text-sm font-semibold text-gray-700 mb-4">購買課程</h4>
                  <div className="space-y-4">
                    {order.orderItems.map((item) => (
                      <div
                        key={item.orderItemId}
                        className="flex gap-4 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                        onClick={() => router.push(`/courses/${item.courseId}`)}
                      >
                        <div className="relative w-32 h-20 flex-shrink-0 rounded-lg overflow-hidden bg-gray-200">
                          <Image
                            src={item.courseCoverImageUrl}
                            alt={item.courseTitle}
                            fill
                            className="object-cover"
                          />
                        </div>
                        <div className="flex-1 min-w-0">
                          <h5 className="text-lg font-semibold text-gray-900 mb-1 truncate">
                            {item.courseTitle}
                          </h5>
                          <p className="text-gray-600">
                            NT$ {item.price.toLocaleString()}
                          </p>
                        </div>
                        <div className="flex items-center">
                          <button className="px-4 py-2 text-blue-600 hover:text-blue-700 font-semibold">
                            查看課程 →
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
