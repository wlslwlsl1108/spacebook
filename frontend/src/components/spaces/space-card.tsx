import Link from "next/link";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import type { SpaceListItem } from "@/types";

const spaceTypeLabel: Record<SpaceListItem["spaceType"], string> = {
  STUDY: "스터디",
  PARTY: "파티",
  MEETING: "회의",
};

interface SpaceCardProps {
  space: SpaceListItem;
}

export default function SpaceCard({ space }: SpaceCardProps) {
  return (
    <Card className="overflow-hidden">
      <div className="h-48 bg-muted flex items-center justify-center text-muted-foreground text-sm">
        {space.imageUrl ? space.spaceName : "이미지 없음"}
      </div>

      <CardContent className="pt-4 space-y-2">
        <h3 className="font-semibold text-lg">{space.spaceName}</h3>

        <div className="flex items-center gap-2">
          <Badge variant="outline">{spaceTypeLabel[space.spaceType]}</Badge>
          <span className="text-sm text-muted-foreground">
            최대 {space.capacity}명
          </span>
        </div>

        <p className="text-sm text-muted-foreground">{space.location}</p>
        <p className="font-medium">
          {space.pricePerHour.toLocaleString()}원 / 시간
        </p>
      </CardContent>

      <CardFooter>
        <Link href={`/spaces/${space.id}`} className="w-full">
          <Button className="w-full">상세 보기</Button>
        </Link>
      </CardFooter>
    </Card>
  );
}
