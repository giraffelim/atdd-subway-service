package nextstep.subway.favorite.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.BaseEntity;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.domain.Station;

@Entity
public class Favorite extends BaseEntity {
    private static final String CAN_NOT_SOURCE_TARGET_IS_SAME = "출발역과 도착역이 동일한 경우 즐겨찾기로 등록할 수 없습니다.";
    private static final String CAN_NOT_OPERATION_IS_NOT_OWNER = "본인의 즐겨찾기가 아닌 경우 작업을 진행할 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_station_id")
    private Station source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_station_id")
    private Station target;

    protected Favorite() {

    }

    public Favorite(Member member, Station source, Station target) {
        validateSourceAndTargetIsSame(source, target);
        this.member = member;
        this.source = source;
        this.target = target;
    }

    private void validateSourceAndTargetIsSame(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(CAN_NOT_SOURCE_TARGET_IS_SAME);
        }
    }

    public void isOperateByOwner(Member requestMember) {
        if (!member.equals(requestMember)) {
            throw new IllegalArgumentException(CAN_NOT_OPERATION_IS_NOT_OWNER);
        }
    }

    public Long getId() {
        return id;
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }
}
