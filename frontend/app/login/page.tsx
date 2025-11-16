import OAuthButton from '@/components/auth/OAuthButton';

/**
 * Login page with OAuth authentication options.
 * Users can sign in with Google or Facebook.
 */
export default function LoginPage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-amber-50/50 to-white flex items-center justify-center px-4">
      <div className="max-w-md w-full">
        {/* Logo and Title */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-green-700 mb-2">
            地球軟體學院
          </h1>
          <p className="text-gray-600">
            歡迎回來，請選擇登入方式
          </p>
        </div>

        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">
            登入
          </h2>

          {/* OAuth Buttons */}
          <div className="space-y-4">
            <OAuthButton
              provider="google"
              label="使用 Google 登入"
            />

            <OAuthButton
              provider="facebook"
              label="使用 Facebook 登入"
            />
          </div>

          {/* Privacy Notice */}
          <div className="mt-6 text-center text-sm text-gray-500">
            <p>
              登入即表示您同意我們的
              <a href="/terms/service" className="text-green-600 hover:underline ml-1">
                服務條款
              </a>
              和
              <a href="/terms/privacy" className="text-green-600 hover:underline ml-1">
                隱私政策
              </a>
            </p>
          </div>
        </div>

        {/* Additional Info */}
        <div className="mt-8 text-center text-gray-600">
          <p className="mb-2">首次登入將自動建立帳號</p>
          <p className="text-sm">使用相同 Email 登入不同平台將自動合併帳號</p>
        </div>
      </div>
    </div>
  );
}
