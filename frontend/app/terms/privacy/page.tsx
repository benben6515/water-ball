/**
 * Privacy Policy Page
 * Displays the platform's privacy policy and data protection practices
 */
export default function PrivacyPolicyPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg p-8 md:p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-4">隱私權政策</h1>
            <p className="text-gray-600">最後更新日期：2025年11月21日</p>
          </div>

          {/* Content */}
          <div className="prose prose-lg max-w-none">
            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">1. 政策說明</h2>
              <p className="text-gray-700 mb-4">
                地球軟體學院（以下簡稱「本平台」）非常重視您的隱私權保護。本隱私權政策說明我們如何收集、使用、揭露及保護您的個人資料。
              </p>
              <p className="text-gray-700">
                當您使用本平台服務時，即表示您同意本隱私權政策的內容。如果您不同意本政策的任何內容，請停止使用本平台服務。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">2. 個人資料的收集</h2>
              <p className="text-gray-700 mb-4">我們可能收集以下類型的個人資料：</p>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">2.1 註冊資料</h3>
              <ul className="list-disc list-inside text-gray-700 space-y-2 mb-4">
                <li>電子郵件地址</li>
                <li>暱稱</li>
                <li>OAuth 提供者資訊（Google、Facebook）</li>
                <li>個人檔案資訊（性別、生日、地點、職業、GitHub 連結等）</li>
              </ul>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">2.2 使用資料</h3>
              <ul className="list-disc list-inside text-gray-700 space-y-2 mb-4">
                <li>課程觀看記錄與進度</li>
                <li>練習題完成狀況</li>
                <li>遊戲化系統數據（等級、經驗值、成就）</li>
                <li>訂單與購買記錄</li>
                <li>第三方帳號連結資訊（Discord、GitHub）</li>
              </ul>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">2.3 技術資料</h3>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>IP 位址</li>
                <li>瀏覽器類型與版本</li>
                <li>裝置資訊</li>
                <li>Cookie 與類似追蹤技術</li>
                <li>存取時間與頻率</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">3. 個人資料的使用</h2>
              <p className="text-gray-700 mb-4">我們使用您的個人資料用於以下目的：</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>提供、維護及改善本平台服務</li>
                <li>處理您的註冊與帳號管理</li>
                <li>處理課程購買與訂單</li>
                <li>記錄學習進度與成就</li>
                <li>提供客戶服務與技術支援</li>
                <li>發送重要通知與更新</li>
                <li>分析使用趨勢以改善服務品質</li>
                <li>防止詐欺與濫用行為</li>
                <li>遵守法律義務</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">4. 個人資料的分享</h2>
              <p className="text-gray-700 mb-4">
                我們不會出售您的個人資料。我們僅在以下情況分享您的個人資料：
              </p>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">4.1 服務提供商</h3>
              <p className="text-gray-700 mb-4">
                我們可能與協助我們營運平台的第三方服務提供商分享資料，包括：
              </p>
              <ul className="list-disc list-inside text-gray-700 space-y-2 mb-4">
                <li>雲端主機服務商</li>
                <li>支付處理商</li>
                <li>分析工具提供商（Google Analytics、Microsoft Clarity）</li>
                <li>電子郵件服務提供商</li>
              </ul>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">4.2 法律要求</h3>
              <p className="text-gray-700 mb-4">在法律要求或必要時，我們可能揭露您的資料以：</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2 mb-4">
                <li>遵守法律程序或政府要求</li>
                <li>保護本平台的權利與財產</li>
                <li>防止或調查可能的違法行為</li>
                <li>保護使用者或公眾的安全</li>
              </ul>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">4.3 業務轉讓</h3>
              <p className="text-gray-700">
                如本平台進行合併、收購或資產出售，您的個人資料可能會轉讓給相關實體。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">5. Cookie 與追蹤技術</h2>
              <p className="text-gray-700 mb-4">本平台使用 Cookie 和類似技術來改善使用者體驗：</p>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">5.1 Cookie 類型</h3>
              <ul className="list-disc list-inside text-gray-700 space-y-2 mb-4">
                <li>
                  <strong>必要 Cookie</strong>：確保網站正常運作（如登入狀態）
                </li>
                <li>
                  <strong>功能 Cookie</strong>：記住您的偏好設定
                </li>
                <li>
                  <strong>分析 Cookie</strong>：了解使用者如何使用網站
                </li>
                <li>
                  <strong>廣告 Cookie</strong>：提供相關的廣告內容
                </li>
              </ul>

              <h3 className="text-xl font-semibold text-gray-900 mb-3">5.2 管理 Cookie</h3>
              <p className="text-gray-700">
                您可以透過瀏覽器設定管理或刪除 Cookie。但請注意，停用某些 Cookie 可能影響網站功能。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">6. 資料安全</h2>
              <p className="text-gray-700 mb-4">我們採取適當的技術與組織措施保護您的個人資料：</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>使用 SSL/TLS 加密傳輸</li>
                <li>資料庫加密存儲</li>
                <li>定期安全性評估與更新</li>
                <li>存取控制與權限管理</li>
                <li>員工資料保護訓練</li>
              </ul>
              <p className="text-gray-700 mt-4">
                然而，沒有任何網路傳輸或電子儲存方式是 100%
                安全的。我們會盡力保護您的資料，但無法保證絕對安全。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">7. 資料保存</h2>
              <p className="text-gray-700">
                我們僅在達成收集目的所需的期間內保存您的個人資料。當您關閉帳號或要求刪除資料時，我們將在合理期間內刪除您的個人資料，除非法律要求我們保留更長時間。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">8. 您的權利</h2>
              <p className="text-gray-700 mb-4">根據適用的資料保護法律，您享有以下權利：</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>
                  <strong>存取權</strong>：查看我們持有的您的個人資料
                </li>
                <li>
                  <strong>更正權</strong>：要求更正不正確的資料
                </li>
                <li>
                  <strong>刪除權</strong>：要求刪除您的個人資料
                </li>
                <li>
                  <strong>限制處理權</strong>：限制我們處理您的資料
                </li>
                <li>
                  <strong>資料可攜權</strong>：要求以結構化格式提供資料
                </li>
                <li>
                  <strong>反對權</strong>：反對我們處理您的資料
                </li>
              </ul>
              <p className="text-gray-700 mt-4">
                如欲行使上述權利，請透過本政策末尾的聯絡方式與我們聯繫。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">9. 兒童隱私</h2>
              <p className="text-gray-700">
                本平台不針對 13 歲以下兒童提供服務，我們不會故意收集 13
                歲以下兒童的個人資料。如果您發現我們收集了兒童的個人資料，請立即聯繫我們，我們將盡快刪除相關資料。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">10. 第三方連結</h2>
              <p className="text-gray-700">
                本平台可能包含第三方網站的連結（如
                Discord、GitHub、Facebook）。我們不對這些第三方網站的隱私實務負責。建議您查看這些網站的隱私權政策。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">11. 政策變更</h2>
              <p className="text-gray-700">
                我們可能不時更新本隱私權政策。任何變更將在本頁面公布，重大變更我們會以電子郵件或網站通知的方式告知您。建議您定期查閱本頁面以了解最新的隱私權政策。
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">12. 聯絡我們</h2>
              <p className="text-gray-700 mb-4">
                如果您對本隱私權政策有任何疑問、意見或要求，請透過以下方式聯繫我們：
              </p>
              <div className="bg-gray-50 rounded-lg p-6">
                <p className="text-gray-700 mb-2">
                  <strong>地球軟體學院</strong>
                </p>
                <p className="text-gray-700 mb-2">電子郵件：privacy@waterballsa.tw</p>
                <p className="text-gray-700">客服信箱：support@waterballsa.tw</p>
              </div>
            </section>

            <section className="mt-8 pt-8 border-t border-gray-200">
              <p className="text-sm text-gray-600 italic">
                本隱私權政策符合台灣個人資料保護法及相關法規要求。我們承諾保護您的隱私權，並依法處理您的個人資料。
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}
