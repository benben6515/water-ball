import Link from 'next/link';

export default function Footer() {
  return (
    <footer className="border-t py-12 px-6 text-sm text-gray-600">
      <div className="container mx-auto max-w-4xl">
        <div className="flex flex-col md:flex-row justify-between gap-8">
          <div>
            <p className="font-semibold text-black mb-2">地球軟體學院</p>
            <p>&copy; {new Date().getFullYear()}</p>
          </div>

          <div className="flex gap-8">
            <div>
              <Link href="/terms/privacy" className="hover:text-green-600 transition-colors block mb-2">
                隱私權政策
              </Link>
              <Link href="/terms/service" className="hover:text-green-600 transition-colors block">
                服務條款
              </Link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
