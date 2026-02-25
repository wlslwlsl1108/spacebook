"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import { Button } from "@/components/ui/button";

const navItems = [
  { href: "/", label: "홈" },
];

// 로그인 시에만 보이는 메뉴
const authNavItems = [
  { href: "/mypage", label: "마이페이지" },
];

export default function Header() {
  const pathname = usePathname();
  const { user, isLoading, logout } = useAuth();

  return (
    <header className="border-b bg-white sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link href="/" className="text-xl font-bold tracking-tight">
          SpaceBook
        </Link>

        <nav className="flex items-center gap-1">
          {navItems.map((item) => (
            <Link key={item.href} href={item.href}>
              <Button
                variant={pathname === item.href ? "default" : "ghost"}
                size="sm"
              >
                {item.label}
              </Button>
            </Link>
          ))}

          {user &&
            authNavItems.map((item) => (
              <Link key={item.href} href={item.href}>
                <Button
                  variant={pathname === item.href ? "default" : "ghost"}
                  size="sm"
                >
                  {item.label}
                </Button>
              </Link>
            ))}

          {!isLoading && (
            <>
              {user ? (
                <Button variant="outline" size="sm" onClick={logout}>
                  로그아웃
                </Button>
              ) : (
                <>
                  <Link href="/login">
                    <Button
                      variant={pathname === "/login" ? "default" : "ghost"}
                      size="sm"
                    >
                      로그인
                    </Button>
                  </Link>
                  <Link href="/signup">
                    <Button
                      variant={pathname === "/signup" ? "default" : "outline"}
                      size="sm"
                    >
                      회원가입
                    </Button>
                  </Link>
                </>
              )}
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
