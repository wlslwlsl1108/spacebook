"use client";

import { useState, useEffect, useCallback } from "react";
import { useAuth } from "@/hooks/use-auth";
import { getSpaces, getRecommendations } from "@/lib/api";
import type { SpaceListItem, PageResponse, SpaceSearchParams } from "@/types";
import SpaceCard from "@/components/spaces/space-card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const spaceTypes = [
  { value: "ALL", label: "전체" },
  { value: "STUDY", label: "스터디" },
  { value: "PARTY", label: "파티" },
  { value: "MEETING", label: "회의" },
] as const;

const sortOptions = [
  { value: "createdAt,DESC", label: "최신순" },
  { value: "pricePerHour,ASC", label: "가격 낮은순" },
  { value: "pricePerHour,DESC", label: "가격 높은순" },
] as const;

export default function HomePage() {
  const { user } = useAuth();

  // 검색/필터/정렬 상태
  const [searchInput, setSearchInput] = useState("");
  const [selectedType, setSelectedType] = useState("ALL");
  const [selectedSort, setSelectedSort] = useState("createdAt,DESC");

  // 공간 목록 상태
  const [spaces, setSpaces] = useState<SpaceListItem[]>([]);
  const [page, setPage] = useState(0);
  const [isLast, setIsLast] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // AI 추천 상태
  const [aiQuery, setAiQuery] = useState("");
  const [aiResults, setAiResults] = useState<SpaceListItem[]>([]);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState("");

  // 공간 목록 조회
  const fetchSpaces = useCallback(
    async (pageNum: number, append: boolean) => {
      setIsLoading(true);
      try {
        const params: SpaceSearchParams = {
          page: pageNum,
          size: 9,
          sort: selectedSort,
        };
        if (searchInput.trim()) params.location = searchInput.trim();
        if (selectedType !== "ALL")
          params.spaceType = selectedType as SpaceSearchParams["spaceType"];

        const res = await getSpaces(params);
        const data: PageResponse<SpaceListItem> = res.data;

        setSpaces((prev) => (append ? [...prev, ...data.content] : data.content));
        setIsLast(data.last);
        setPage(data.number);
      } catch {
        setSpaces([]);
      } finally {
        setIsLoading(false);
      }
    },
    [searchInput, selectedSort, selectedType],
  );

  // 초기 로드 + 필터/정렬 변경 시 재조회
  useEffect(() => {
    fetchSpaces(0, false);
  }, [fetchSpaces]);

  // 검색 실행
  const handleSearch = () => {
    fetchSpaces(0, false);
  };

  // 더 보기
  const handleLoadMore = () => {
    fetchSpaces(page + 1, true);
  };

  // AI 추천
  const handleRecommend = async () => {
    if (!aiQuery.trim()) return;
    setAiLoading(true);
    setAiError("");
    setAiResults([]);
    try {
      const res = await getRecommendations(aiQuery.trim());
      setAiResults(res.data);
    } catch (err) {
      setAiError(err instanceof Error ? err.message : "추천 요청에 실패했습니다.");
    } finally {
      setAiLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-10">
      {/* AI 추천 섹션 (로그인 시만) */}
      {user && (
        <section className="text-center space-y-4">
          <h1 className="text-3xl font-bold">어떤 공간을 찾고 계세요?</h1>
          <p className="text-muted-foreground">
            원하는 조건을 자연어로 입력하면 AI가 최적의 공간을 추천해드립니다.
          </p>
          <div className="max-w-xl mx-auto flex gap-2">
            <Input
              placeholder="예: 강남에서 10명이 회의할 수 있는 공간"
              value={aiQuery}
              onChange={(e) => setAiQuery(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleRecommend()}
            />
            <Button onClick={handleRecommend} disabled={aiLoading}>
              {aiLoading ? "추천 중..." : "추천 받기"}
            </Button>
          </div>

          {aiError && <p className="text-sm text-destructive">{aiError}</p>}

          {aiResults.length > 0 && (
            <div className="space-y-4 text-left">
              <h2 className="text-xl font-bold">
                AI 추천 결과{" "}
                <Badge variant="secondary">{aiResults.length}건</Badge>
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {aiResults.map((space) => (
                  <SpaceCard key={space.id} space={space} />
                ))}
              </div>
            </div>
          )}

          <Separator />
        </section>
      )}

      {/* 공간 목록 섹션 */}
      <section className="space-y-6">
        <h2 className="text-2xl font-bold">공간 목록</h2>

        {/* 검색 + 필터 + 정렬 */}
        <div className="space-y-4">
          {/* 검색바 */}
          <div className="flex gap-2">
            <Input
              placeholder="위치 또는 공간 이름으로 검색..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            />
            <Button onClick={handleSearch}>검색</Button>
          </div>

          {/* 필터 + 정렬 */}
          <div className="flex items-center justify-between flex-wrap gap-2">
            <div className="flex gap-2 flex-wrap">
              {spaceTypes.map((type) => (
                <Button
                  key={type.value}
                  variant={selectedType === type.value ? "default" : "outline"}
                  size="sm"
                  onClick={() => setSelectedType(type.value)}
                >
                  {type.label}
                </Button>
              ))}
            </div>

            <Select value={selectedSort} onValueChange={setSelectedSort}>
              <SelectTrigger className="w-40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {sortOptions.map((opt) => (
                  <SelectItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* 공간 카드 그리드 */}
        {isLoading && spaces.length === 0 ? (
          <p className="text-center text-muted-foreground py-12">
            불러오는 중...
          </p>
        ) : spaces.length === 0 ? (
          <p className="text-center text-muted-foreground py-12">
            조건에 맞는 공간이 없습니다.
          </p>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {spaces.map((space) => (
                <SpaceCard key={space.id} space={space} />
              ))}
            </div>

            {/* 더 보기 */}
            {!isLast && (
              <div className="text-center">
                <Button
                  variant="outline"
                  onClick={handleLoadMore}
                  disabled={isLoading}
                >
                  {isLoading ? "불러오는 중..." : "더 보기"}
                </Button>
              </div>
            )}
          </>
        )}
      </section>
    </div>
  );
}
