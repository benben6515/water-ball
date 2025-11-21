/**
 * Terms of Service Page
 * Displays the platform's terms of service and usage guidelines
 */
export default function TermsOfServicePage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg p-8 md:p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-4">服務條款</h1>
            <p className="text-gray-600">最後更新日期：2025年11月21日</p>
          </div>

          {/* Content */}
          <div className="prose prose-lg max-w-none">
            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">1. 服務說明</h2>
              <p className="text-gray-700 mb-4">
                歡迎使用地球軟體學院（以下簡稱「本平台」）。本平台提供線上程式教育課程、影片內容、實作練習及相關學習資源。使用本平台服務前，請仔細閱讀本服務條款。
              </p>
              <p className="text-gray-700">
                當您註冊帳號或使用本平台任何服務時，即表示您已閱讀、瞭解並同意接受本服務條款之所有內容。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">2. 帳號註冊與使用</h2>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>您必須提供真實、準確、最新及完整的個人資料</li>
                <li>您有責任維護帳號密碼的機密性</li>
                <li>一個帳號僅供一人使用，不得與他人共用</li>
                <li>您須對所有使用您帳號的行為負責</li>
                <li>若發現帳號遭未經授權使用，請立即通知本平台</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">3. 課程使用規範</h2>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>購買的課程僅供個人學習使用</li>
                <li>禁止下載、重製、散布或公開傳輸課程內容</li>
                <li>禁止將課程內容用於商業用途</li>
                <li>禁止分享帳號或課程存取權限給他人</li>
                <li>課程內容的著作權歸本平台或其授權人所有</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">4. 付費服務</h2>
              <p className="text-gray-700 mb-4">
                本平台提供免費及付費課程。付費課程需完成購買程序後方可觀看。
              </p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>所有價格均以新台幣（TWD）計價</li>
                <li>付款完成後，課程將立即解鎖供您觀看</li>
                <li>除法律另有規定外，數位內容購買後不接受退款</li>
                <li>本平台保留隨時調整課程價格的權利</li>
                <li>優惠券使用規則詳見各優惠券說明</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">5. 智慧財產權</h2>
              <p className="text-gray-700 mb-4">
                本平台所有內容，包括但不限於文字、圖片、影音、程式碼、設計、商標等，均受著作權法、商標法及其他相關法律保護。
              </p>
              <p className="text-gray-700">
                未經本平台書面同意，您不得重製、改作、散布、公開傳輸或以其他方式利用本平台內容。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">6. 使用者行為規範</h2>
              <p className="text-gray-700 mb-4">使用本平台時，您同意不會：</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>上傳、發布或傳輸任何非法、有害、威脅、辱罵、騷擾、侵權的內容</li>
                <li>冒充他人或虛偽陳述與他人之關係</li>
                <li>干擾或破壞本平台服務或伺服器</li>
                <li>使用自動化程式或機器人存取本平台</li>
                <li>嘗試未經授權存取其他使用者帳號或本平台系統</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">7. 服務變更與終止</h2>
              <p className="text-gray-700 mb-4">
                本平台保留隨時修改、暫停或終止全部或部分服務的權利，恕不另行通知。
              </p>
              <p className="text-gray-700">
                若您違反本服務條款，本平台有權暫停或終止您的帳號，且不予退費。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">8. 免責聲明</h2>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>本平台課程內容僅供參考，不保證學習成效</li>
                <li>本平台不對因使用或無法使用服務所造成的損害負責</li>
                <li>本平台不保證服務不會中斷或完全沒有錯誤</li>
                <li>本平台不對第三方網站或服務的內容負責</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">9. 準據法與管轄權</h2>
              <p className="text-gray-700">
                本服務條款之解釋與適用，以及與本服務條款有關的爭議，均應依照中華民國法律予以處理，並以台灣台北地方法院為第一審管轄法院。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">10. 條款修改</h2>
              <p className="text-gray-700">
                本平台保留隨時修改本服務條款的權利。修改後的條款將公布於本頁面，並於公布時立即生效。建議您定期查閱本頁面以了解最新的服務條款。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">11. 聯絡方式</h2>
              <p className="text-gray-700">若您對本服務條款有任何疑問，請透過以下方式聯繫我們：</p>
              <p className="text-gray-700 mt-4">電子郵件：support@waterballsa.tw</p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}
