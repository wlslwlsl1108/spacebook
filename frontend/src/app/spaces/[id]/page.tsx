"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import { getSpaceDetail } from "@/lib/api";
import type { Space } from "@/types";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";

const spaceTypeLabel: Record<Space["spaceType"], string> = {
  STUDY: "스터디",
  PARTY: "파티",
  MEETING: "회의",
};

export default function SpaceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();

  const [space, setSpace] = useState<Space | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function fetchSpace() {
      try {
        const res = await getSpaceDetail(Number(id));
        setSpace(res.data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "공간 정보를 불러올 수 없습니다.");
      } finally {
        setIsLoading(false);
      }
    }
    fetchSpace();
  }, [id]);

  const handleReservation = () => {
    if (!user) {
      router.push("/login");
      return;
    }
    router.push(`/spaces/${id}/reserve`);
  };

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <p className="text-center text-muted-foreground py-12">불러오는 중...</p>
      </div>
    );
  }

  if (error || !space) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8 text-center space-y-4">
        <p className="text-muted-foreground py-12">{error || "공간을 찾을 수 없습니다."}</p>
        <Button variant="outline" onClick={() => router.push("/")}>
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* 공간 정보 */}
        <div className="lg:col-span-2 space-y-6">
          <div className="h-64 bg-muted rounded-lg flex items-center justify-center text-muted-foreground overflow-hidden">
            {space.imageUrl ? (
              <img
                src={space.imageUrl}
                alt={space.spaceName}
                className="w-full h-full object-cover rounded-lg"
              />
            ) : (
              `${space.spaceName} 이미지`
            )}
          </div>

          <div>
            <div className="flex items-center gap-3 mb-2">
              <h1 className="text-2xl font-bold">{space.spaceName}</h1>
              <Badge>예약 가능</Badge>
            </div>
            <p className="text-muted-foreground">{space.location}</p>
          </div>

          <Separator />

          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-sm text-muted-foreground">유형</p>
              <p className="font-medium">{spaceTypeLabel[space.spaceType]}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">수용 인원</p>
              <p className="font-medium">최대 {space.capacity}명</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">시간당 가격</p>
              <p className="font-medium">{space.pricePerHour.toLocaleString()}원</p>
            </div>
          </div>

          {space.description && (
            <>
              <Separator />
              <div>
                <h2 className="text-lg font-semibold mb-2">공간 소개</h2>
                <p className="text-muted-foreground whitespace-pre-line">{space.description}</p>
              </div>
            </>
          )}
        </div>

        {/* 예약 카드 */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>예약하기</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="text-2xl font-bold">
                {space.pricePerHour.toLocaleString()}원
                <span className="text-sm font-normal text-muted-foreground"> / 시간</span>
              </div>

              <Button className="w-full" onClick={handleReservation}>
                {user ? "예약하기" : "로그인 후 예약하기"}
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
