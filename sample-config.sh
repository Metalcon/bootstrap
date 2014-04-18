METALCON_PREFIX="/home/`whoami`/git/metalcon"

SDD_ENABLED=true
UMS_ENABLED=true
IGS_ENABLED=true
LIKE_ENABLED=true
# UID service always enabled

# paths MUST be children of METALCON_PREFIX
SDD_PATH="${METALCON_PREFIX}/staticDataDeliveryServer"
UMS_PATH="${METALCON_PREFIX}/urlMappingServer"
IGS_PATH="${METALCON_PREFIX}/imageGalleryServer"
LIKE_PATH="${METALCON_PREFIX}/likeButtonServer"
UID_PATH="${METALCON_PREFIX}/muid"

LOG_DIR="/usr/share/metalcon/log"
