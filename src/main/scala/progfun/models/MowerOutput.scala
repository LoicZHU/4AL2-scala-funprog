package progfun.models

final case class MowerOutput(
    start: PositionOutput,
    instructions: List[Char],
    end: PositionOutput)
