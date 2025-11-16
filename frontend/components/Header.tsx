import Link from 'next/link';
import Logo from './Logo';

export default function Header() {
  return (
    <header className="border-b border-amber-200 bg-amber-50/30">
      <nav className="container mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <Link href="/" className="flex items-center gap-3 text-xl font-semibold text-green-700 hover:text-green-600 transition-colors">
            <Logo className="w-10 h-10" />
            <span>地球軟體學院</span>
          </Link>
          <div className="flex gap-8 text-sm">
            <Link href="/" className="hover:text-green-600 transition-colors">
              首頁
            </Link>
            <Link href="/courses" className="hover:text-green-600 transition-colors">
              課程
            </Link>
            <Link href="/about" className="hover:text-green-600 transition-colors">
              關於
            </Link>
          </div>
        </div>
      </nav>
    </header>
  );
}
