import Foundation

struct StickerBook {
    static let stickers = [
        "🌟", "⭐️", "✨", "🎉", "🎊",
        "🏆", "🥇", "🎖", "👏", "💪",
        "🔥", "💯", "✅", "🎯", "🚀",
        "🌈", "🦄", "🎨", "🌺", "🌸",
        "🎭", "🎪", "🎢", "🎡", "🎠",
        "🍀", "🌻", "🦋", "🐝", "🌞"
    ]
    
    static func randomSticker() -> String {
        return stickers.randomElement() ?? "⭐️"
    }
}
